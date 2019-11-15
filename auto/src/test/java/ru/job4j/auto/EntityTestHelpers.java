package ru.job4j.auto;

import ru.job4j.auto.model.*;

import java.time.Instant;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static com.google.common.base.Throwables.getRootCause;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.job4j.auto.TestModelData.*;

public class EntityTestHelpers {
    public static final EntityTestHelper<Body> BODY_TEST_HELPER = new EntityTestHelper<>(false) {
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

    public static final EntityTestHelper<Engine> ENGINE_TEST_HELPER = new EntityTestHelper<>(false) {
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

    public static final EntityTestHelper<Transmission> TRANSMISSION_TEST_HELPER = new EntityTestHelper<>(false) {
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

    public static final EntityTestHelper<Vendor> VENDOR_TEST_HELPER = new EntityTestHelper<>(false) {
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

    public static final EntityTestHelper<Car> CAR_TEST_HELPER = new EntityTestHelper<>(false) {
        @Override
        public Car doCopy(Car entity) {
            return new Car(entity.getId(), VENDOR_TEST_HELPER.nullableCopy(entity.getVendor()), entity.getModel(), entity.getYear(),
                    entity.getMileage(), BODY_TEST_HELPER.nullableCopy(entity.getBody()), ENGINE_TEST_HELPER.nullableCopy(entity.getEngine()),
                    TRANSMISSION_TEST_HELPER.nullableCopy(entity.getTransmission()));
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

    public static final EntityTestHelper<Post> POST_TEST_HELPER = new EntityTestHelper<>(true, "posted", "user") {
        @Override
        public Post doCopy(Post entity) {
            return new Post(entity.getId(), entity.getTitle(), entity.getMessage(), entity.getPosted(), entity.getPrice(), CAR_TEST_HELPER.nullableCopy(entity.getCar()),
                    copyRecursionPrevent(entity.getUser(), User::getPosts, User::setPosts, USER_TEST_HELPER::nullableCopy));
        }

        @Override
        public Post newEntity() {
            return new Post(null, "Another title", "Another message", Instant.now(), 1000, CAR_TEST_HELPER.newEntity(), USER);
        }

        @Override
        public Post editedEntity(Post post) {
            var edited = doCopy(post);
            edited.setTitle("updated title");
            edited.setMessage("updated description");
            edited.setPrice(200000);
            edited.setCar(CAR_TEST_HELPER.editedEntity(post.getCar()));
            return post;
        }
    };

    public static final EntityTestHelper<User> USER_TEST_HELPER = new EntityTestHelper<>(true, "registered", "posts") {
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
