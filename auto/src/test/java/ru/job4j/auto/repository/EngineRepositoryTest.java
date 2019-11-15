package ru.job4j.auto.repository;

import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import org.junit.jupiter.api.Test;
import ru.job4j.auto.inject.BasicRepositoryModule;
import ru.job4j.auto.model.Engine;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertNull;
import static ru.job4j.auto.EntityTestHelpers.ENGINE_TEST_HELPER;
import static ru.job4j.auto.TestModelData.DIESEL;
import static ru.job4j.auto.TestModelData.ENGINES;

@IncludeModule(BasicRepositoryModule.class)
class EngineRepositoryTest extends AbstractBaseRepositoryTest {
    @Inject
    private EngineRepository repository;

    @Test
    void create() {
        var newEngine = ENGINE_TEST_HELPER.newEntity();
        Engine saved = repository.save(ENGINE_TEST_HELPER.copy(newEngine));
        var newId = saved.getId();
        newEngine.setId(newId);
        ENGINE_TEST_HELPER.assertMatch(saved, newEngine);
        ENGINE_TEST_HELPER.assertMatch(repository.find(newId), newEngine);
    }

    @Test
    void update() {
        var engineToUpdate = ENGINE_TEST_HELPER.editedEntity(DIESEL);
        Engine saved = repository.save(ENGINE_TEST_HELPER.copy(engineToUpdate));

        ENGINE_TEST_HELPER.assertMatch(saved, engineToUpdate);
        ENGINE_TEST_HELPER.assertMatch(repository.find(DIESEL.getId()), engineToUpdate);
    }

    @Test
    void delete() {
        var id = DIESEL.getId();
        repository.delete(id);
        assertNull(repository.find(id));
    }

    @Test
    void find() {
        var body = repository.find(DIESEL.getId());
        ENGINE_TEST_HELPER.assertMatch(body, DIESEL);
    }

    @Test
    void findAll() {
        var bodies = repository.findAll();
        ENGINE_TEST_HELPER.assertMatch(bodies, ENGINES);
    }
}