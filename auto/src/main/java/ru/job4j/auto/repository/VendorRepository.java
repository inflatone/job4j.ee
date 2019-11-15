package ru.job4j.auto.repository;

import ru.job4j.auto.repository.env.JpaManager;
import ru.job4j.auto.model.Vendor;

import javax.inject.Inject;

public class VendorRepository extends BaseEntityRepository<Vendor> {
    @Inject
    public VendorRepository(JpaManager jm) {
        super(Vendor.class, jm);
    }
}
