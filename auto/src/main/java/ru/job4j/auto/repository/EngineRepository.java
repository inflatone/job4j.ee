package ru.job4j.auto.repository;

import ru.job4j.auto.repository.env.JpaManager;
import ru.job4j.auto.model.Engine;

import javax.inject.Inject;

public class EngineRepository extends BaseEntityRepository<Engine> {
    @Inject
    private EngineRepository(JpaManager jm) {
        super(Engine.class, jm);
    }
}
