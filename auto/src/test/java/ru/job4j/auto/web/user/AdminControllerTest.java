package ru.job4j.auto.web.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import ru.job4j.auto.config.helper.BaseEntityTestHelper;
import ru.job4j.auto.config.helper.BaseToTestHelper;
import ru.job4j.auto.model.User;
import ru.job4j.auto.service.UserService;
import ru.job4j.auto.to.UserTo;
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

    private final BaseToTestHelper<UserTo, User> toTestHelper;

    private final UserService service;

    @Autowired
    AdminControllerTest(BaseEntityTestHelper<User> testHelper, BaseToTestHelper<UserTo, User> toTestHelper, UserService service) {
        super(AdminController.URL);
        this.testHelper = testHelper;
        this.toTestHelper = toTestHelper;
        this.service = service;
    }

    @Test
    void create() throws Exception {
        var newUser = testHelper.newEntity();
        ResultActions actions = perform(doPost().userAsFormData(newUser).auth(DEALER))
                .andDo(print())
                .andExpect(status().isCreated());
        newUser.setId(getCreatedResourceId(actions.andReturn()));

        actions.andExpect(toTestHelper.forAuth(DEALER).contentJson(newUser));
        testHelper.assertMatch(service.findAll(), DEALER, USER, newUser);
    }

    @Test
    void update() throws Exception {
        var userToUpdate = testHelper.editedEntity(USER);
        perform(doPost(USER.getId()).userAsFormData(userToUpdate).auth(DEALER))
                .andDo(print())
                .andExpect(status().isNoContent());
        testHelper.assertMatch(service.findAll(), DEALER, userToUpdate);
    }

    @Test
    void enable() throws Exception {
        perform(doPut(USER.getId(), "enabled").auth(DEALER).jsonBody(false))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertFalse(service.find(USER.getId()).isEnabled());
    }

    @Test
    void delete() throws Exception {
        perform(doDelete(USER.getId()).auth(DEALER))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> service.find(USER.getId()));
    }
}
