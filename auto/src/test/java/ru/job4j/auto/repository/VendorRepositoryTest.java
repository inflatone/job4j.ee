package ru.job4j.auto.repository;

import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import org.junit.jupiter.api.Test;
import ru.job4j.auto.inject.BasicRepositoryModule;
import ru.job4j.auto.model.Vendor;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertNull;
import static ru.job4j.auto.EntityTestHelpers.VENDOR_TEST_HELPER;
import static ru.job4j.auto.TestModelData.MAZDA;
import static ru.job4j.auto.TestModelData.VENDORS;

@IncludeModule(BasicRepositoryModule.class)
class VendorRepositoryTest extends AbstractBaseRepositoryTest {
    @Inject
    private VendorRepository repository;

    @Test
    void create() {
        var newVendor = VENDOR_TEST_HELPER.newEntity();
        Vendor saved = repository.save(VENDOR_TEST_HELPER.copy(newVendor));
        var newId = saved.getId();
        newVendor.setId(newId);
        VENDOR_TEST_HELPER.assertMatch(saved, newVendor);
        VENDOR_TEST_HELPER.assertMatch(repository.find(newId), newVendor);
    }

    @Test
    void update() {
        var vendorToUpdate = VENDOR_TEST_HELPER.editedEntity(MAZDA);
        Vendor saved = repository.save(VENDOR_TEST_HELPER.copy(vendorToUpdate));

        VENDOR_TEST_HELPER.assertMatch(saved, vendorToUpdate);
        VENDOR_TEST_HELPER.assertMatch(repository.find(MAZDA.getId()), vendorToUpdate);
    }

    @Test
    void delete() {
        var id = MAZDA.getId();
        repository.delete(id);
        assertNull(repository.find(id));
    }

    @Test
    void find() {
        var vendor = repository.find(MAZDA.getId());
        VENDOR_TEST_HELPER.assertMatch(vendor, MAZDA);
    }

    @Test
    void findAll() {
        var vendors = repository.findAll();
        VENDOR_TEST_HELPER.assertMatch(vendors, VENDORS);
    }
}
