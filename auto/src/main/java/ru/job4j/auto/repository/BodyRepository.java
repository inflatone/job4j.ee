package ru.job4j.auto.repository;

import ru.job4j.auto.repository.env.JpaManager;
import ru.job4j.auto.model.Body;

import javax.inject.Inject;

public class BodyRepository extends BaseEntityRepository<Body> {
    @Inject
    public BodyRepository(JpaManager jm) {
        super(Body.class, jm);
    }
}
