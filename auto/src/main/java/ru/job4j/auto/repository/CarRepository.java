package ru.job4j.auto.repository;

import ru.job4j.auto.repository.env.JpaManager;
import ru.job4j.auto.model.Car;

import javax.inject.Inject;

public class CarRepository extends BaseEntityRepository<Car> {
    @Inject
    public CarRepository(JpaManager jm) {
        super(Car.class, jm);
    }
}