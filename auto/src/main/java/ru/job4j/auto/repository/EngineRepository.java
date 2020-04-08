package ru.job4j.auto.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.auto.model.Engine;

@Repository
public class EngineRepository extends BaseEntityRepository<Engine> {
    public EngineRepository() {
        super(Engine.class);
    }
}
