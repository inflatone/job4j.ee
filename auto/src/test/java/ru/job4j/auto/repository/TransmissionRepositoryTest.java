package ru.job4j.auto.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.job4j.auto.BaseEntityTestHelper;
import ru.job4j.auto.model.Transmission;

import static org.junit.jupiter.api.Assertions.assertNull;
import static ru.job4j.auto.TestModelData.AUTOMATIC;
import static ru.job4j.auto.TestModelData.MANUAL;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
class TransmissionRepositoryTest extends AbstractCarDetailsRepositoryTest {
    private final TransmissionRepository repository;

    private final BaseEntityTestHelper<Transmission> testHelper;

    @Test
    void create() {
        var newTransmission = testHelper.newEntity();
        Transmission saved = repository.save(testHelper.copy(newTransmission));
        var newId = saved.getId();
        newTransmission.setId(newId);
        testHelper.assertMatch(saved, newTransmission);
        testHelper.assertMatch(repository.find(newId), newTransmission);
    }

    @Test
    void update() {
        var transmissionToUpdate = testHelper.editedEntity(TRANSMISSION);
        Transmission saved = repository.save(testHelper.copy(transmissionToUpdate));

        testHelper.assertMatch(saved, transmissionToUpdate);
        testHelper.assertMatch(repository.find(TRANSMISSION.getId()), transmissionToUpdate);
    }

    @Test
    void delete() {
        var id = TRANSMISSION.getId();
        repository.delete(id);
        assertNull(repository.find(id));
    }

    @Test
    void find() {
        var transmission = repository.find(AUTOMATIC.getId());
        testHelper.assertMatch(transmission, AUTOMATIC);
    }

    @Test
    void findAll() {
        var transmissions = repository.findAll();
        testHelper.assertMatch(transmissions, AUTOMATIC, MANUAL, TRANSMISSION);
    }
}
