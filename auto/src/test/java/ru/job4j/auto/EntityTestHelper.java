package ru.job4j.auto;

import com.fasterxml.jackson.core.type.TypeReference;
import one.util.streamex.StreamEx;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import ru.job4j.auto.web.converter.JsonHelper;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class EntityTestHelper<T, ID> {
    private final JsonHelper jsonHelper;

    private final Class<T> clazz;
    private final TypeReference<Map<ID, T>> mapTypeReference;

    private final boolean checkRecursively;
    private final String[] fieldsToIgnore;

    protected EntityTestHelper(JsonHelper jsonHelper, Class<T> clazz, TypeReference<Map<ID, T>> mapTypeReference, boolean checkRecursively, String... fieldsToIgnore) {
        this.jsonHelper = jsonHelper;

        this.clazz = clazz;
        this.mapTypeReference = mapTypeReference;

        this.checkRecursively = checkRecursively;
        this.fieldsToIgnore = fieldsToIgnore;
    }

    public void assertMatch(T actual, T expected, String... additionalFieldsToIgnore) {
        var fieldsToIgnore = additionalFieldsToIgnore.length == 0 ? this.fieldsToIgnore
                : StreamEx.of(this.fieldsToIgnore, additionalFieldsToIgnore).flatMap(StreamEx::of).toArray(String.class);
        if (checkRecursively) {
            assertThat(actual).usingRecursiveComparison().ignoringFields(fieldsToIgnore).isEqualTo(expected);
        } else {
            assertThat(actual).isEqualToIgnoringGivenFields(expected, fieldsToIgnore);
        }
    }

    public void assertMatch(Iterable<T> actual, Iterable<T> expected) {
        if (checkRecursively) {
            assertThat(actual).usingRecursiveFieldByFieldElementComparator()
                    .usingElementComparatorIgnoringFields(fieldsToIgnore)
                    .isEqualTo(expected);
        } else {
            assertThat(actual).usingElementComparatorIgnoringFields(fieldsToIgnore).isEqualTo(expected);
        }
    }

    public void assertMatch(Map<ID, T> actual, Collection<T> expected) {
        assertMatch(actual.values(), expected);
        assertThat(actual).containsOnlyKeys(StreamEx.of(expected).map(idMapper()).toList());
    }

    public ResultMatcher contentJson(T expected) {
        return result -> assertMatch(readFromJsonMvcResult(result), expected);
    }

    public ResultMatcher contentJson(Iterable<T> expected) {
        return result -> assertMatch(readListFromJsonMvcResult(result), expected);
    }

    public ResultMatcher contentMapJson(Collection<T> expected) {
        return result -> {
            Map<ID, T> actual = readMapFromJsonMvcResult(result);
            assertMatch(actual, expected);
        };
    }

    @SafeVarargs
    public final void assertMatch(Map<ID, T> actual, T... expected) {
        assertMatch(actual, List.of(expected));
    }

    @SafeVarargs
    public final void assertMatch(Iterable<T> actual, T... expected) {
        assertMatch(actual, List.of(expected));
    }

    @SafeVarargs
    public final ResultMatcher contentJson(T... expected) {
        return contentJson(List.of(expected));
    }

    @SafeVarargs
    public final ResultMatcher contentMapJson(T... expected) {
        return contentMapJson(List.of(expected));
    }

    public T nullableCopy(T entity) {
        return entity == null ? null : doCopy(entity);
    }

    public T copy(T entity) {
        return doCopy(Objects.requireNonNull(entity, entity + " must be not null"));
    }

    protected abstract Function<T, ID> idMapper();

    protected abstract T doCopy(T entity);

    public abstract T newEntity();

    public abstract T editedEntity(T t);

    public T readFromJsonMvcResult(MvcResult result) throws UnsupportedEncodingException {
        return jsonHelper.fromJson(getContent(result), clazz);
    }

    public List<T> readListFromJsonMvcResult(MvcResult result) throws UnsupportedEncodingException {
        return jsonHelper.listFromJson(getContent(result), clazz);
    }

    private Map<ID, T> readMapFromJsonMvcResult(MvcResult result) throws UnsupportedEncodingException {
        return jsonHelper.mapFromJson(getContent(result), mapTypeReference);
    }

    private static String getContent(MvcResult result) throws UnsupportedEncodingException {
        return result.getResponse().getContentAsString();
    }
}
