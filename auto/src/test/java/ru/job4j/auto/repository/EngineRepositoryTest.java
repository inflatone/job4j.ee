package ru.job4j.auto.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.job4j.auto.BaseEntityTestHelper;
import ru.job4j.auto.model.Engine;

import static org.junit.jupiter.api.Assertions.assertNull;
import static ru.job4j.auto.TestModelData.DIESEL;
import static ru.job4j.auto.TestModelData.ELECTRIC;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
class EngineRepositoryTest extends AbstractCarDetailsRepositoryTest {
    private final EngineRepository repository;

    private final BaseEntityTestHelper<Engine> testHelper;

    @Test
    void create() {
        var newEngine = testHelper.newEntity();
        Engine saved = repository.save(testHelper.copy(newEngine));
        var newId = saved.getId();
        newEngine.setId(newId);
        testHelper.assertMatch(saved, newEngine);
        testHelper.assertMatch(repository.find(newId), newEngine);
    }

    @Test
    void update() {
        var engineToUpdate = testHelper.editedEntity(ENGINE);
        Engine saved = repository.save(testHelper.copy(engineToUpdate));

        testHelper.assertMatch(saved, engineToUpdate);
        testHelper.assertMatch(repository.find(ENGINE.getId()), engineToUpdate);
    }

    @Test
    void delete() {
        var id = ENGINE.getId();
        repository.delete(id);
        assertNull(repository.find(id));
    }

    @Test
    void find() {
        var body = repository.find(ENGINE.getId());
        testHelper.assertMatch(body, ENGINE);
    }

    @Test
    void findAll() {
        var bodies = repository.findAll();
        testHelper.assertMatch(bodies, DIESEL, ELECTRIC, ENGINE);
    }
}