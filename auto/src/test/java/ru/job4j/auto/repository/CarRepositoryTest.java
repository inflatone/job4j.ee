package ru.job4j.auto.repository;

import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import org.junit.jupiter.api.Test;
import ru.job4j.auto.inject.ExtendedRepositoryModule;
import ru.job4j.auto.model.Car;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertNull;
import static ru.job4j.auto.EntityTestHelpers.CAR_TEST_HELPER;
import static ru.job4j.auto.EntityTestHelpers.validateRootCause;
import static ru.job4j.auto.TestModelData.CARS;
import static ru.job4j.auto.TestModelData.CAR_BMW;

@IncludeModule(ExtendedRepositoryModule.class)
class CarRepositoryTest extends AbstractBaseRepositoryTest {
    @Inject
    private CarRepository repository;

    @Test
    void create() {
        var newCar = CAR_TEST_HELPER.newEntity();
        Car saved = repository.save(CAR_TEST_HELPER.copy(newCar));
        var newId = saved.getId();
        newCar.setId(newId);
        CAR_TEST_HELPER.assertMatch(saved, newCar);
        CAR_TEST_HELPER.assertMatch(repository.find(newId), newCar);
    }

    @Test
    void update() {
        var carToUpdate = CAR_TEST_HELPER.editedEntity(CAR_BMW);
        Car saved = repository.save(CAR_TEST_HELPER.copy(carToUpdate));

        CAR_TEST_HELPER.assertMatch(saved, carToUpdate);
        CAR_TEST_HELPER.assertMatch(repository.find(CAR_BMW.getId()), carToUpdate);
    }

    @Test
    void delete() {
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
        CAR_TEST_HELPER.assertMatch(car, CAR_BMW);
    }


    @Test
    void findAll() {
        var cars = repository.findAll();
        CAR_TEST_HELPER.assertMatch(cars, CARS);
    }
}