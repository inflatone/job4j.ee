package ru.job4j.auto.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.auto.config.helper.BaseEntityTestHelper;
import ru.job4j.auto.model.Car;
import ru.job4j.auto.model.Post;

import javax.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertNull;
import static ru.job4j.auto.config.TestHelpersConfig.validateRootCause;
import static ru.job4j.auto.TestModelData.CAR_BMW;
import static ru.job4j.auto.TestModelData.POST_BMW;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = {"classpath:db/import/data.sql"}, config = @SqlConfig(encoding = "UTF-8"))
class CarRepositoryTest extends AbstractBaseRepositoryTest {
    private final CarRepository repository;

    private final BaseEntityTestHelper<Car> testHelper;

    @Test
    void create() {
        var newCar = testHelper.newEntity();
        Car saved = repository.save(testHelper.copy(newCar));
        var newId = saved.getId();
        newCar.setId(newId);
        testHelper.assertMatch(saved, newCar);
        testHelper.assertMatch(newCar, repository.findWithDetails(newId));
    }

    @Test
    @Transactional
    void update() {
        var carToUpdate = testHelper.editedEntity(CAR_BMW);
        Car saved = repository.save(testHelper.copy(carToUpdate));

        testHelper.assertMatch(saved, carToUpdate);
        testHelper.assertMatch(repository.findWithDetails(CAR_BMW.getId()), carToUpdate);
    }

    @Test
    void delete(@Autowired PostRepository postRepository, @Autowired BaseEntityTestHelper<Post> postEntityTestHelper) {
        Post post = postEntityTestHelper.copy(POST_BMW);
        post.setCar(null); // detach a car from post (to delete the car)
        postRepository.save(post);
        repository.delete(CAR_BMW.getId());
        assertNull(repository.find(CAR_BMW.getId()));
    }

    @Test
    void deleteNotFound() {
        validateRootCause(EntityNotFoundException.class, () -> repository.delete(0));
    }

    @Test
    void find() {
        var car = repository.find(CAR_BMW.getId());
        testHelper.assertMatch(car, CAR_BMW, "body", "engine", "transmission", "vendor");
    }
}