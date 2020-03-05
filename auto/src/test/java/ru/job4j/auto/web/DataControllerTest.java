package ru.job4j.auto.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.job4j.auto.EntityTestHelper;
import ru.job4j.auto.model.Role;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
}
