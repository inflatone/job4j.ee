package ru.job4j.auto.repository;

import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import org.junit.jupiter.api.Test;
import ru.job4j.auto.inject.BasicRepositoryModule;
import ru.job4j.auto.model.Body;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertNull;
import static ru.job4j.auto.EntityTestHelpers.BODY_TEST_HELPER;
import static ru.job4j.auto.TestModelData.BODIES;
import static ru.job4j.auto.TestModelData.SEDAN;

@IncludeModule(BasicRepositoryModule.class)
class BodyRepositoryTest extends AbstractBaseRepositoryTest {
    @Inject
    private BodyRepository repository;

    @Test
    void create() {
        var newBody = BODY_TEST_HELPER.newEntity();
        Body saved = repository.save(BODY_TEST_HELPER.copy(newBody));
        var newId = saved.getId();
        newBody.setId(newId);
        BODY_TEST_HELPER.assertMatch(saved, newBody);
        BODY_TEST_HELPER.assertMatch(repository.find(newId), newBody);
    }

    @Test
    void update() {
        var bodyToUpdate = BODY_TEST_HELPER.editedEntity(SEDAN);
        Body saved = repository.save(BODY_TEST_HELPER.copy(bodyToUpdate));

        BODY_TEST_HELPER.assertMatch(saved, bodyToUpdate);
        BODY_TEST_HELPER.assertMatch(repository.find(SEDAN.getId()), bodyToUpdate);
    }

    @Test
    void delete() {
        var id = SEDAN.getId();
        repository.delete(id);
        assertNull(repository.find(id));
    }

    @Test
    void find() {
        var body = repository.find(SEDAN.getId());
        BODY_TEST_HELPER.assertMatch(body, SEDAN);
    }

    @Test
    void findAll() {
        var bodies = repository.findAll();
        BODY_TEST_HELPER.assertMatch(bodies, BODIES);
    }
}
