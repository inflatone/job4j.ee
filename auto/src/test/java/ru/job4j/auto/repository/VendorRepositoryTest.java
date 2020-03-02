package ru.job4j.auto.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.job4j.auto.EntityTestHelper;
import ru.job4j.auto.model.Vendor;

import static org.junit.jupiter.api.Assertions.assertNull;
import static ru.job4j.auto.TestModelData.BMW;
import static ru.job4j.auto.TestModelData.MAZDA;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
class VendorRepositoryTest extends AbstractCarDetailsRepositoryTest {
    private final VendorRepository repository;

    private final EntityTestHelper<Vendor> testHelper;

    @Test
    void create() {
        var newVendor = testHelper.newEntity();
        Vendor saved = repository.save(testHelper.copy(newVendor));
        var newId = saved.getId();
        newVendor.setId(newId);
        testHelper.assertMatch(saved, newVendor);
        testHelper.assertMatch(repository.find(newId), newVendor);
    }

    @Test
    void update() {
        var vendorToUpdate = testHelper.editedEntity(MAZDA);
        Vendor saved = repository.save(testHelper.copy(vendorToUpdate));

        testHelper.assertMatch(saved, vendorToUpdate);
        testHelper.assertMatch(repository.find(MAZDA.getId()), vendorToUpdate);
    }

    @Test
    void delete() {
        var id = VENDOR.getId();
        repository.delete(id);
        assertNull(repository.find(id));
    }

    @Test
    void find() {
        var vendor = repository.find(MAZDA.getId());
        testHelper.assertMatch(vendor, MAZDA);
    }

    @Test
    void findAll() {
        var vendors = repository.findAll();
        testHelper.assertMatch(vendors, MAZDA, BMW, VENDOR);
    }
}
