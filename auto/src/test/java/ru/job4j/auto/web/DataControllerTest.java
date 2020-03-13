package ru.job4j.auto.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.job4j.auto.BaseEntityTestHelper;
import ru.job4j.auto.EntityTestHelper;
import ru.job4j.auto.model.*;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.job4j.auto.TestModelData.*;

class DataControllerTest extends AbstractControllerTest {
    DataControllerTest() {
        super(DataController.URL);
    }

    @Test
    void roles(@Autowired EntityTestHelper<Role, String> testHelper) throws Exception {
        perform(doGet("roles"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeIsJson())
                .andExpect(testHelper.contentMapJson(Role.USER));
    }

    @Test
    void rolesAsAdmin(@Autowired EntityTestHelper<Role, String> testHelper) throws Exception {
        perform(doGet("roles").auth(DEALER))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeIsJson())
                .andExpect(testHelper.contentMapJson(Role.USER, Role.ADMIN));
    }

    @Test
    void bodies(@Autowired BaseEntityTestHelper<Body> testHelper) throws Exception {
        perform(doGet("bodies"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeIsJson())
                .andExpect(testHelper.contentMapJson(BODIES));
    }

    @Test
    void engines(@Autowired BaseEntityTestHelper<Engine> testHelper) throws Exception {
        perform(doGet("engines"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeIsJson())
                .andExpect(testHelper.contentMapJson(ENGINES));
    }

    @Test
    void transmissions(@Autowired BaseEntityTestHelper<Transmission> testHelper) throws Exception {
        perform(doGet("transmissions"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeIsJson())
                .andExpect(testHelper.contentMapJson(TRANSMISSIONS));
    }

    @Test
    void vendors(@Autowired BaseEntityTestHelper<Vendor> testHelper) throws Exception {
        perform(doGet("vendors"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeIsJson())
                .andExpect(testHelper.contentMapJson(VENDORS));
    }
}
