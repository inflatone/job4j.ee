package ru.job4j.auto.repository;

import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import org.junit.jupiter.api.Test;
import ru.job4j.auto.inject.BasicRepositoryModule;
import ru.job4j.auto.model.Transmission;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertNull;
import static ru.job4j.auto.EntityTestHelpers.TRANSMISSION_TEST_HELPER;
import static ru.job4j.auto.TestModelData.*;

@IncludeModule(BasicRepositoryModule.class)
class TransmissionRepositoryTest extends AbstractBaseRepositoryTest {
    @Inject
    private TransmissionRepository repository;

    @Test
    void create() {
        var newTransmission = TRANSMISSION_TEST_HELPER.newEntity();
        Transmission saved = repository.save(TRANSMISSION_TEST_HELPER.copy(newTransmission));
        var newId = saved.getId();
        newTransmission.setId(newId);
        TRANSMISSION_TEST_HELPER.assertMatch(saved, newTransmission);
        TRANSMISSION_TEST_HELPER.assertMatch(repository.find(newId), newTransmission);
    }

    @Test
    void update() {
        var transmissionToUpdate = TRANSMISSION_TEST_HELPER.editedEntity(MANUAL);
        Transmission saved = repository.save(TRANSMISSION_TEST_HELPER.copy(transmissionToUpdate));

        TRANSMISSION_TEST_HELPER.assertMatch(saved, transmissionToUpdate);
        TRANSMISSION_TEST_HELPER.assertMatch(repository.find(MANUAL.getId()), transmissionToUpdate);
    }

    @Test
    void delete() {
        var id = MANUAL.getId();
        repository.delete(id);
        assertNull(repository.find(id));
    }

    @Test
    void find() {
        var transmission = repository.find(AUTOMATIC.getId());
        TRANSMISSION_TEST_HELPER.assertMatch(transmission, AUTOMATIC);
    }

    @Test
    void findAll() {
        var transmissions = repository.findAll();
        TRANSMISSION_TEST_HELPER.assertMatch(transmissions, TRANSMISSIONS);
    }
}
