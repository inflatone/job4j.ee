package ru.job4j.auto.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.job4j.auto.BaseEntityTestHelper;
import ru.job4j.auto.EntityTestHelper;
import ru.job4j.auto.model.*;
import ru.job4j.auto.util.exception.NotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.job4j.auto.TestModelData.*;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
class DataServiceTest extends AbstractServiceTest {
    private final DataService service;

    @Test
    void findRoles(@Autowired EntityTestHelper<Role, String> testHelper) {
        testHelper.assertMatch(service.findAvailableRoles(Role.USER), Role.USER);
    }

    @Test
    void findRolesAsAdmin(@Autowired EntityTestHelper<Role, String> testHelper) {
        testHelper.assertMatch(service.findAvailableRoles(Role.ADMIN), Role.USER, Role.ADMIN);
    }

    @Test
    void findRolesAsNull() {
        var thrown = assertThrows(NotFoundException.class, () -> service.findAvailableRoles(null));
        assertEquals("Role must not be null", thrown.getMessage());
    }

    @Test
    void findVendors(@Autowired BaseEntityTestHelper<Vendor> testHelper) {
        testHelper.assertMatch(service.findAvailableVendors(), VENDORS);
    }

    @Test
    void findBodies(@Autowired BaseEntityTestHelper<Body> testHelper) {
        testHelper.assertMatch(service.findAvailableCarBodies(), BODIES);
    }

    @Test
    void findEngines(@Autowired BaseEntityTestHelper<Engine> testHelper) {
        testHelper.assertMatch(service.findAvailableCarEngines(), ENGINES);
    }

    @Test
    void findTransmissions(@Autowired BaseEntityTestHelper<Transmission> testHelper) {
        testHelper.assertMatch(service.findAvailableCarTransmissions(), TRANSMISSIONS);
    }
}
