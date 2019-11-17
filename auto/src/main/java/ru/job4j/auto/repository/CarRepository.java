package ru.job4j.auto.repository;

import ru.job4j.auto.model.Car;
import ru.job4j.auto.repository.env.JpaManager;

import javax.inject.Inject;
import java.util.Map;

import static org.hibernate.graph.GraphSemantic.FETCH;

public class CarRepository extends BaseEntityRepository<Car> {
    @Inject
    public CarRepository(JpaManager jm) {
        super(Car.class, jm);
    }

    /**
     * Finds a car entity associated with the given id, also fetches its details
     *
     * @param id id
     * @return car entity
     */
    public Car findWithDetails(int id) {
        return jm.transactionalRetrieve(em ->
                em.find(entityClass, id, Map.of(FETCH.getJpaHintName(),
                        em.createEntityGraph(Car.WITH_DETAILS)))
        );
    }
}