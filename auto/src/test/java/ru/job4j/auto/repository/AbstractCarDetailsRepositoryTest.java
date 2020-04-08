package ru.job4j.auto.repository;

import org.junit.jupiter.api.AfterAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import ru.job4j.auto.model.Body;
import ru.job4j.auto.model.Engine;
import ru.job4j.auto.model.Transmission;
import ru.job4j.auto.model.Vendor;

@Sql(scripts = {"classpath:db/import/data.sql", "classpath:/db/data_repository.sql"}, config = @SqlConfig(encoding = "UTF-8"))
abstract class AbstractCarDetailsRepositoryTest extends AbstractBaseRepositoryTest {
    // need car details that aren't bound with any cars (to test deleting 'em)
    protected static final Body BODY = new Body(37, "Minivan");

    protected static final Engine ENGINE = new Engine(25, "Unknown");

    protected static final Transmission TRANSMISSION = new Transmission(12, "Hybrid");

    protected static final Vendor VENDOR = new Vendor(53, "Ford", "USA", null);

    /**
     * To prevent the errors in controller tests bound with existing these "extra" car details
     * appearing due a specific (random?) order of test classes launch (TestNG we need)
     */
    @AfterAll
    static void clean(@Autowired BodyRepository bodyRepository,
                      @Autowired EngineRepository engineRepository,
                      @Autowired TransmissionRepository transmissionRepository,
                      @Autowired VendorRepository vendorRepository) {
        bodyRepository.delete(BODY.getId());
        engineRepository.delete(ENGINE.getId());
        transmissionRepository.delete(TRANSMISSION.getId());
        vendorRepository.delete(VENDOR.getId());
    }
}
