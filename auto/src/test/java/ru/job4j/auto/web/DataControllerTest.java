package ru.job4j.auto.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.job4j.auto.config.helper.BaseDetailsTestHelperFacade;
import ru.job4j.auto.config.helper.BaseEntityTestHelper.RoleEntityTestHelper;
import ru.job4j.auto.model.Role;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.job4j.auto.TestModelData.*;

class DataControllerTest extends AbstractControllerTest {
    DataControllerTest() {
        super(DataController.URL);
    }

    @Test
    void roles(@Autowired RoleEntityTestHelper testHelper) throws Exception {
        perform(doGet("roles"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeIsJson())
                .andExpect(testHelper.contentJsonAsMap(Role.USER));
    }

    @Test
    void rolesAsAdmin(@Autowired RoleEntityTestHelper testHelper) throws Exception {
        perform(doGet("roles").auth(DEALER))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeIsJson())
                .andExpect(testHelper.contentJsonAsMap(Role.USER, Role.ADMIN));
    }

    @Test
    void details(@Autowired BaseDetailsTestHelperFacade carDetailsTestHelper) throws Exception {
        perform(doGet("details"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeIsJson())
                .andExpect(carDetailsTestHelper.contentMapJson(BODIES, ENGINES, TRANSMISSIONS, VENDORS));
    }
}
