package ru.job4j.auto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.job4j.auto.model.*;

import java.time.Instant;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static com.google.common.base.Throwables.getRootCause;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.job4j.auto.TestModelData.*;

@Configuration
public class EntityTestHelpers {
    @Bean("bodyHelper")
    public EntityTestHelper<Body> bodyEntityTestHelper() {
        return new BaseEntityTestHelper<>(false) {
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
    public EntityTestHelper<Engine> engineEntityTestHelper() {
        return new BaseEntityTestHelper<>(false) {
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
    public EntityTestHelper<Transmission> transmissionEntityTestHelper() {
        return new BaseEntityTestHelper<>(false) {
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
    public EntityTestHelper<Vendor> vendorEntityTestHelper() {
        return new BaseEntityTestHelper<>(false) {
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
    public EntityTestHelper<Car> carEntityTestHelper(@Autowired EntityTestHelper<Body> bodyEntityTestHelper,
                                                     @Autowired EntityTestHelper<Engine> engineEntityTestHelper,
                                                     @Autowired EntityTestHelper<Transmission> transmissionEntityTestHelper,
                                                     @Autowired EntityTestHelper<Vendor> vendorEntityTestHelper
    ) {
        return new BaseEntityTestHelper<>(false) {
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
    public EntityTestHelper<Post> postEntityTestHelper(@Autowired EntityTestHelper<User> userEntityTestHelper,
                                                       @Autowired EntityTestHelper<Car> carEntityTestHelper) {
        return new BaseEntityTestHelper<>(true, "posted", "user") {
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
    public EntityTestHelper<User> userEntityTestHelper() {
        return new BaseEntityTestHelper<>(true, "registered", "password", "posts") {
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
