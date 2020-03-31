package ru.job4j.auto.config.helper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.sun.xml.bind.v2.model.core.ID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import one.util.streamex.StreamEx;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import ru.job4j.auto.model.*;
import ru.job4j.auto.web.converter.JsonHelper;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class BaseDetailsTestHelperFacade {
    private static final TypeReference<Map<String, Object>> JSON_AS_PROPERTY_MAP_REFERENCE = new TypeReference<>() {};

    private final JsonHelper jsonHelper;

    @Getter
    private final CarDetailTestHelper<Body> bodyEntityTestHelper;

    @Getter
    private final CarDetailTestHelper<Engine> engineEntityTestHelper;

    @Getter
    private final CarDetailTestHelper<Transmission> transmissionEntityTestHelper;

    @Getter
    private final CarDetailTestHelper<Vendor> vendorEntityTestHelper;

    public BaseDetailsTestHelperFacade(JsonHelper jsonHelper) {
        this.jsonHelper = jsonHelper;
        this.bodyEntityTestHelper = new BodyEntityTestHelper(jsonHelper);
        this.engineEntityTestHelper = new EngineEntityTestHelper(jsonHelper);
        this.transmissionEntityTestHelper = new TransmissionEntityTestHelper(jsonHelper);
        this.vendorEntityTestHelper = new VendorEntityTestHelper(jsonHelper);
    }

    public ResultMatcher contentMapJson(Collection<Body> bodies,
                                        Collection<Engine> engines,
                                        Collection<Transmission> transmissions,
                                        Collection<Vendor> vendors) {
        return result -> {
            Map<String, Object> details = retrieveAsPropertyMap(result);
            bodyEntityTestHelper.assertMatch(jsonHelper.asJson(details.get("bodies")), bodies);
            engineEntityTestHelper.assertMatch(jsonHelper.asJson(details.get("engines")), engines);
            transmissionEntityTestHelper.assertMatch(jsonHelper.asJson(details.get("transmissions")), transmissions);
            vendorEntityTestHelper.assertMatch(jsonHelper.asJson(details.get("vendors")), vendors);
        };
    }

    private Map<String, Object> retrieveAsPropertyMap(MvcResult result) throws UnsupportedEncodingException {
        return jsonHelper.mapFromJson(result.getResponse().getContentAsString(), JSON_AS_PROPERTY_MAP_REFERENCE);
    }

    public abstract static class JsonMappableTestHelper<K, V> extends AbstractEntityTestHelper<V> {
        private final TypeReference<Map<K, V>> mapTypeReference;

        public JsonMappableTestHelper(JsonHelper jsonHelper, Class<V> clazz, TypeReference<Map<K, V>> mapTypeReference,
                                      boolean checkRecursively, String... fieldsToIgnore) {
            super(jsonHelper, clazz, checkRecursively, fieldsToIgnore);
            this.mapTypeReference = mapTypeReference;
        }

        /**
         * The function used to produce the key for each value
         * @return key function
         */
        protected abstract Function<V, K> keyFunction();

        public ResultMatcher contentJsonAsMap(Collection<V> expected) {
            return result -> {
                Map<K, V> actual = readMapFromJsonMvcResult(result);
                assertMatch(actual, expected);
            };
        }

        @SafeVarargs
        public final ResultMatcher contentJsonAsMap(V... expected) {
            return contentJsonAsMap(List.of(expected));
        }

        public void assertMatch(Map<K, V> actual, Collection<V> expected) {
            assertMatch(actual.values(), expected);
            assertThat(actual).containsOnlyKeys(StreamEx.of(expected).map(keyFunction()).toList());
        }

        @SafeVarargs
        public final void assertMatch(Map<K, V> actual, V... expected) {
            assertMatch(actual, List.of(expected));
        }

        protected Map<K, V> readAsMap(String json) {
            return jsonHelper.mapFromJson(json, mapTypeReference);
        }

        private Map<K, V> readMapFromJsonMvcResult(MvcResult result) throws UnsupportedEncodingException {
            return readAsMap(getContent(result));
        }
    }

    public abstract static class CarDetailTestHelper<T extends BaseEntity> extends JsonMappableTestHelper<Integer, T> {
        public CarDetailTestHelper(JsonHelper jsonHelper, Class<T> clazz, TypeReference<Map<Integer, T>> typeReference) {
            super(jsonHelper, clazz, typeReference, false);
        }

        @Override
        protected Function<T, Integer> keyFunction() {
            return BaseEntity::getId;
        }

        public void assertMatch(String json, Collection<T> expected) {
            assertMatch(readAsMap(json), expected);
        }
    }

    private static class BodyEntityTestHelper extends CarDetailTestHelper<Body> {
        public BodyEntityTestHelper(JsonHelper jsonHelper) {
            super(jsonHelper, Body.class, new TypeReference<>() {});
        }

        @Override
        public Body doCopy(Body entity) {
            return new Body(entity.getId(), entity.getType());
        }

        @Override
        public Body newEntity() {
            return new Body(null, "Other");
        }

        @Override
        public Body editedEntity(Body body) {
            var edited = copy(body);
            edited.setType("Edited body");
            return edited;
        }
    }

    private static class EngineEntityTestHelper extends CarDetailTestHelper<Engine> {
        EngineEntityTestHelper(JsonHelper jsonHelper) {
            super(jsonHelper, Engine.class, new TypeReference<>() {});
        }

        @Override
        public Engine doCopy(Engine entity) {
            return new Engine(entity.getId(), entity.getType());
        }

        @Override
        public Engine newEntity() {
            return new Engine(null, "Other");
        }

        @Override
        public Engine editedEntity(Engine engine) {
            var edited = doCopy(engine);
            edited.setType("Edited engine");
            return edited;
        }
    }

    private static class TransmissionEntityTestHelper extends CarDetailTestHelper<Transmission> {
        TransmissionEntityTestHelper(JsonHelper jsonHelper) {
            super(jsonHelper, Transmission.class, new TypeReference<>() {});
        }

        @Override
        public Transmission doCopy(Transmission entity) {
            return new Transmission(entity.getId(), entity.getType());
        }

        @Override
        public Transmission newEntity() {
            return new Transmission(null, "Other");
        }

        @Override
        public Transmission editedEntity(Transmission transmission) {
            var edited = doCopy(transmission);
            edited.setType("Edited transmission");
            return edited;
        }
    }

    private static class VendorEntityTestHelper extends CarDetailTestHelper<Vendor> {
        VendorEntityTestHelper(JsonHelper jsonHelper) {
            super(jsonHelper, Vendor.class, new TypeReference<>() {});
        }

        @Override
        public Vendor doCopy(Vendor entity) {
            return new Vendor(entity.getId(), entity.getName(), entity.getCountry(), entity.getLogoLink());
        }

        @Override
        public Vendor newEntity() {
            return new Vendor(null, "Other Vendor", "USA", "logo");
        }

        @Override
        public Vendor editedEntity(Vendor vendor) {
            var edited = doCopy(vendor);
            edited.setName("Edited Vendor");
            edited.setCountry("Edited country");
            return edited;
        }
    }
}
