package ru.job4j.auto.config.helper;

import ru.job4j.auto.model.Car;
import ru.job4j.auto.model.Post;
import ru.job4j.auto.model.Role;
import ru.job4j.auto.model.User;
import ru.job4j.auto.web.converter.JsonHelper;

import java.time.Instant;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

import static ru.job4j.auto.TestModelData.*;

public class EntityTestHelpers {
    public static class UserEntityTestHelper extends BaseEntityTestHelper<User> {
        public UserEntityTestHelper(JsonHelper jsonHelper) {
            super(jsonHelper, User.class,
                    true, "registered", "password", "posts");
        }

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
    }

    public static class CarEntityTestHelper extends BaseEntityTestHelper<Car> {
        private final BaseDetailsTestHelperFacade carDetailsTestHelper;

        public CarEntityTestHelper(JsonHelper jsonHelper, BaseDetailsTestHelperFacade carDetailsTestHelper) {
            super(jsonHelper, Car.class, false);
            this.carDetailsTestHelper = carDetailsTestHelper;
        }

        @Override
        public Car doCopy(Car entity) {
            return new Car(entity.getId(), carDetailsTestHelper.getVendorEntityTestHelper().nullableCopy(entity.getVendor()),
                    entity.getModel(), entity.getYear(), entity.getMileage(),
                    carDetailsTestHelper.getBodyEntityTestHelper().nullableCopy(entity.getBody()),
                    carDetailsTestHelper.getEngineEntityTestHelper().nullableCopy(entity.getEngine()),
                    carDetailsTestHelper.getTransmissionEntityTestHelper().nullableCopy(entity.getTransmission()));
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
    }

    public static class PostEntityTestHelper extends BaseEntityTestHelper<Post> {
        private final BaseEntityTestHelper<User> userTestHelper;

        private final BaseEntityTestHelper<Car> carTestHelper;


        public PostEntityTestHelper(JsonHelper jsonHelper, BaseEntityTestHelper<User> userTestHelper, BaseEntityTestHelper<Car> carTestHelper) {
            super(jsonHelper, Post.class, true, "posted", "user");
            this.userTestHelper = userTestHelper;
            this.carTestHelper = carTestHelper;
        }

        @Override
        public Post doCopy(Post entity) {
            return new Post(entity.getId(), entity.getTitle(), entity.getMessage(), entity.getPosted(), entity.getPrice(), carTestHelper.nullableCopy(entity.getCar()),
                    copyRecursionPrevent(entity.getUser(), User::getPosts, User::setPosts, userTestHelper::nullableCopy));
        }

        @Override
        public Post newEntity() {
            return new Post(null, "Another title", "Another message", Instant.now(), 1000, carTestHelper.newEntity(), null);
        }

        @Override
        public Post editedEntity(Post post) {
            var edited = doCopy(post);
            edited.setTitle("updated title");
            edited.setMessage("updated description");
            edited.setPrice(200000);
            edited.setCar(carTestHelper.editedEntity(post.getCar()));
            return post;
        }
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
}
