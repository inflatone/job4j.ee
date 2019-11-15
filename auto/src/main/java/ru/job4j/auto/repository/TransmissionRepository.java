package ru.job4j.auto.repository;

import ru.job4j.auto.repository.env.JpaManager;
import ru.job4j.auto.model.Transmission;

import javax.inject.Inject;

public class TransmissionRepository extends BaseEntityRepository<Transmission> {
    @Inject
    private TransmissionRepository(JpaManager jm) {
        super(Transmission.class, jm);
    }
}
