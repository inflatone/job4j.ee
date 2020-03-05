package ru.job4j.auto.web.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import ru.job4j.auto.BaseEntityTestHelper;
import ru.job4j.auto.model.User;
import ru.job4j.auto.service.UserService;
import ru.job4j.auto.util.exception.NotFoundException;
import ru.job4j.auto.web.AbstractControllerTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.job4j.auto.TestModelData.DEALER;
import static ru.job4j.auto.TestModelData.USER;

class AdminControllerTest extends AbstractControllerTest {
    private final BaseEntityTestHelper<User> testHelper;

    private final UserService service;

    @Autowired
    AdminControllerTest(BaseEntityTestHelper<User> testHelper, UserService service) {
        super(AdminController.URL);
        this.testHelper = testHelper;
        this.service = service;
    }

    @Test
    void find() throws Exception {
        perform(doGet(USER.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeIsJson())
                .andExpect(testHelper.contentJson(USER));
    }

    @Test
    void findAll() throws Exception {
        perform(doGet())
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeIsJson())
                .andExpect(testHelper.contentJson(DEALER, USER));
    }

    @Test
    void create() throws Exception {
        var newUser = testHelper.newEntity();
        MvcResult result = perform(doPost().userAsFormData(newUser))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();

        newUser.setId(getCreatedResourceId(result));
        testHelper.assertMatch(service.findAll(), DEALER, USER, newUser);
    }

    @Test
    void update() throws Exception {
        var userToUpdate = testHelper.editedEntity(USER);
        perform(doPost().userAsFormData(userToUpdate))
                .andDo(print())
                .andExpect(status().isNoContent());
        testHelper.assertMatch(service.findAll(), DEALER, userToUpdate);
    }

    @Test
    void enable() throws Exception {
        perform(doPost(USER.getId()).unwrap().param("enabled", "false"))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertFalse(service.find(USER.getId()).isEnabled());
    }

    @Test
    void delete() throws Exception {
        perform(doDelete(USER.getId()))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> service.find(USER.getId()));
    }
}
