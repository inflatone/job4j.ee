package ru.job4j.auto.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.auto.model.Body;

@Repository
public class BodyRepository extends BaseEntityRepository<Body> {
    public BodyRepository() {
        super(Body.class);
    }
}
