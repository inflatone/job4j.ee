package ru.job4j.auto.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.job4j.auto.EntityTestHelper;
import ru.job4j.auto.model.Body;

import static org.junit.jupiter.api.Assertions.assertNull;
import static ru.job4j.auto.TestModelData.COUPE;
import static ru.job4j.auto.TestModelData.SEDAN;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BodyRepositoryTest extends AbstractCarDetailsRepositoryTest {
    private final BodyRepository repository;

    private final EntityTestHelper<Body> testHelper;

    @Test
    void create() {
        var newBody = testHelper.newEntity();
        Body saved = repository.save(testHelper.copy(newBody));
        var newId = saved.getId();
        newBody.setId(newId);
        testHelper.assertMatch(saved, newBody);
        testHelper.assertMatch(repository.find(newId), newBody);
    }

    @Test
    void update() {
        var bodyToUpdate = testHelper.editedEntity(BODY);
        Body saved = repository.save(testHelper.copy(bodyToUpdate));

        testHelper.assertMatch(saved, bodyToUpdate);
        testHelper.assertMatch(repository.find(BODY.getId()), bodyToUpdate);
    }

    @Test
    void delete() {
        var id = BODY.getId();
        repository.delete(id);
        assertNull(repository.find(id));
    }

    @Test
    void find() {
        var body = repository.find(BODY.getId());
        testHelper.assertMatch(body, BODY);
    }

    @Test
    void findAll() {
        var bodies = repository.findAll();
        testHelper.assertMatch(bodies, SEDAN, COUPE, BODY);
    }
}
