package ru.job4j.auto.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.auto.model.Car;

import java.util.Map;

import static org.hibernate.graph.GraphSemantic.FETCH;

@Repository
public class CarRepository extends BaseEntityRepository<Car> {
    public CarRepository() {
        super(Car.class);
    }

    /**
     * Finds a car entity associated with the given id, also fetches its details
     *
     * @param id id
     * @return car entity
     */
    public Car findWithDetails(int id) {
        return em.find(entityClass, id, Map.of(FETCH.getJpaHintName(),
                em.createEntityGraph(Car.WITH_DETAILS))
        );
    }
}