package ru.job4j.auto.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.auto.model.Transmission;

@Repository
public class TransmissionRepository extends BaseEntityRepository<Transmission> {
    public TransmissionRepository() {
        super(Transmission.class);
    }
}
