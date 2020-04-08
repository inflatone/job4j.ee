package ru.job4j.auto.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.auto.model.Vendor;

@Repository
public class VendorRepository extends BaseEntityRepository<Vendor> {
    public VendorRepository() {
        super(Vendor.class);
    }
}
