package ru.job4j.auto;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.job4j.auto.model.*;
import ru.job4j.auto.web.converter.JsonHelper;

import java.time.Instant;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static com.google.common.base.Throwables.getRootCause;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.job4j.auto.TestModelData.*;

@Configuration
public class EntityTestHelpers {
    @Autowired(required = false)
    private JsonHelper jsonHelper;

    @Bean
    public BaseEntityTestHelper<Body> bodyEntityTestHelper() {
        return new BaseEntityTestHelper<>(jsonHelper, Body.class, new TypeReference<Map<Integer, Body>>() {}, false) {
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
        };
    }

    @Bean
    public BaseEntityTestHelper<Engine> engineEntityTestHelper() {
        return new BaseEntityTestHelper<>(jsonHelper, Engine.class, new TypeReference<Map<Integer, Engine>>() {}, false) {
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
        };
    }

    @Bean
    public BaseEntityTestHelper<Transmission> transmissionEntityTestHelper() {
        return new BaseEntityTestHelper<>(jsonHelper, Transmission.class, new TypeReference<Map<Integer, Transmission>>() {}, false) {
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
        };
    }

    @Bean
    public BaseEntityTestHelper<Vendor> vendorEntityTestHelper() {
        return new BaseEntityTestHelper<>(jsonHelper, Vendor.class, new TypeReference<Map<Integer, Vendor>>() {}, false) {
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
        };
    }

    @Bean
    public BaseEntityTestHelper<Car> carEntityTestHelper(@Autowired BaseEntityTestHelper<Body> bodyEntityTestHelper,
                                                         @Autowired BaseEntityTestHelper<Engine> engineEntityTestHelper,
                                                         @Autowired BaseEntityTestHelper<Transmission> transmissionEntityTestHelper,
                                                         @Autowired BaseEntityTestHelper<Vendor> vendorEntityTestHelper
    ) {
        return new BaseEntityTestHelper<>(jsonHelper, Car.class, new TypeReference<Map<Integer, Car>>() {}, false) {
            @Override
            public Car doCopy(Car entity) {
                return new Car(entity.getId(), vendorEntityTestHelper.nullableCopy(entity.getVendor()), entity.getModel(), entity.getYear(),
                        entity.getMileage(), bodyEntityTestHelper.nullableCopy(entity.getBody()), engineEntityTestHelper.nullableCopy(entity.getEngine()),
                        transmissionEntityTestHelper.nullableCopy(entity.getTransmission()));
            }

            @Override
            public Car newEntity() {
                return new Car(null, BMW, "M3", 2017, 0, COUPE, DIESEL, AUTOMATIC);
            }

            @Override
            public Car editedEntity(Car car) {
                var edited = doCopy(car);
                edited.setYear(2013);
                edited.setMileage(0);
                edited.setEngine(DIESEL);
                return car;
            }
        };
    }

    @Bean
    public BaseEntityTestHelper<Post> postEntityTestHelper(@Autowired BaseEntityTestHelper<User> userEntityTestHelper,
                                                           @Autowired BaseEntityTestHelper<Car> carEntityTestHelper) {
        return new BaseEntityTestHelper<>(jsonHelper, Post.class, new TypeReference<Map<Integer, Post>>() {}, true, "posted", "user") {
            @Override
            public Post doCopy(Post entity) {
                return new Post(entity.getId(), entity.getTitle(), entity.getMessage(), entity.getPosted(), entity.getPrice(), carEntityTestHelper.nullableCopy(entity.getCar()),
                        copyRecursionPrevent(entity.getUser(), User::getPosts, User::setPosts, userEntityTestHelper::nullableCopy));
            }

            @Override
            public Post newEntity() {
                return new Post(null, "Another title", "Another message", Instant.now(), 1000, carEntityTestHelper.newEntity(), USER);
            }

            @Override
            public Post editedEntity(Post post) {
                var edited = doCopy(post);
                edited.setTitle("updated title");
                edited.setMessage("updated description");
                edited.setPrice(200000);
                edited.setCar(carEntityTestHelper.editedEntity(post.getCar()));
                return post;
            }
        };
    }

    @Bean
    public BaseEntityTestHelper<User> userEntityTestHelper() {
        return new BaseEntityTestHelper<>(jsonHelper, User.class, new TypeReference<Map<Integer, User>>() {}, true, "registered", "password", "posts") {
            @Override
            public User doCopy(User entity) {
                var copy = new User(entity.getId(), entity.getName(), entity.getLogin(), entity.getPassword(), entity.getRole());
                copy.setEnabled(entity.isEnabled());
                copy.setRegistered(entity.getRegistered());
                return copy;
            }

            @Override
            public User newEntity() {
                return new User(null, "Other", "other", "other", Role.USER);
            }

            @Override
            public User editedEntity(User user) {
                var edited = doCopy(user);
                edited.setName("Edited name");
                edited.setPassword("Edited password");
                return edited;
            }
        };
    }

    @Bean // only for entity/json matching
    public EntityTestHelper<Role, String> roleEntityTestHelper() {
        return new EntityTestHelper<>(jsonHelper, Role.class, new TypeReference<Map<String, Role>>() {}, false) {
            @Override
            protected Function<Role, String> idMapper() {
                return Role::name;
            }

            @Override
            protected Role doCopy(Role entity) {
                return entity; // senselessly
            }

            @Override
            public Role newEntity() {
                return Role.USER; // senselessly
            }

            @Override
            public Role editedEntity(Role role) {
                return role;  // senselessly
            }
        };
    }

    private static <P, N> P copyRecursionPrevent(P property, Function<P, N> getter, BiConsumer<P, N> entitySetter, UnaryOperator<P> copier) {
        if (property == null) {
            return null;
        }
        N nestedEntity = getter.apply(property);
        entitySetter.accept(property, null);
        P propertyCopy = copier.apply(property);
        entitySetter.accept(property, nestedEntity);
        return propertyCopy;
    }

    // Check root cause in JUnit: https://github.com/junit-team/junit4/pull/778
    public static <T extends Throwable> void validateRootCause(Class<T> expectedClass, Runnable runnable) {
        assertThrows(expectedClass, () -> {
            try {
                runnable.run();
            } catch (Exception e) {
                throw getRootCause(e);
            }
        });
    }
}
