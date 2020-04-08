package ru.job4j.auto.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.job4j.auto.config.helper.BaseDetailsTestHelperFacade.CarDetailTestHelper;
import ru.job4j.auto.config.helper.BaseEntityTestHelper.RoleEntityTestHelper;
import ru.job4j.auto.model.*;
import ru.job4j.auto.util.exception.NotFoundException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.job4j.auto.TestModelData.*;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
class DataServiceTest extends AbstractServiceTest {
    private final DataService service;

    @Test
    void findRoles(@Autowired RoleEntityTestHelper testHelper) {
        testHelper.assertMatch(service.findAvailableRoles(Role.USER), Role.USER);
    }

    @Test
    void findRolesAsAdmin(@Autowired RoleEntityTestHelper testHelper) {
        testHelper.assertMatch(service.findAvailableRoles(Role.ADMIN), Role.USER, Role.ADMIN);
    }

    @Test
    void findRolesAsNull() {
        var thrown = assertThrows(NotFoundException.class, () -> service.findAvailableRoles(null));
        assertEquals("Role must not be null", thrown.getMessage());
    }

    @Test
    void findVendors(@Autowired CarDetailTestHelper<Vendor> testHelper) {
        testHelper.assertMatch(service.findAvailableVendors(), VENDORS);
    }

    @Test
    void findBodies(@Autowired CarDetailTestHelper<Body> testHelper) {
        testHelper.assertMatch(service.findAvailableCarBodies(), BODIES);
    }

    @Test
    void findEngines(@Autowired CarDetailTestHelper<Engine> testHelper) {
        testHelper.assertMatch(service.findAvailableCarEngines(), ENGINES);
    }

    @Test
    void findTransmissions(@Autowired CarDetailTestHelper<Transmission> testHelper) {
        testHelper.assertMatch(service.findAvailableCarTransmissions(), TRANSMISSIONS);
    }
}
