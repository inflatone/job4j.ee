package ru.job4j.auto.web.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.ResultActions;
import ru.job4j.auto.config.helper.BaseEntityTestHelper;
import ru.job4j.auto.config.helper.BaseToTestHelper;
import ru.job4j.auto.model.User;
import ru.job4j.auto.repository.UserRepository;
import ru.job4j.auto.service.UserService;
import ru.job4j.auto.to.UserTo;
import ru.job4j.auto.util.exception.NotFoundException;
import ru.job4j.auto.web.AbstractControllerTest;
import ru.job4j.auto.web.SecurityHelper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.job4j.auto.TestModelData.DEALER;
import static ru.job4j.auto.TestModelData.USER;

class ProfileControllerTest extends AbstractControllerTest {
    private final BaseEntityTestHelper<User> testHelper;

    private final BaseToTestHelper<UserTo, User> toTestHelper;

    private final UserService service;

    @Autowired
    ProfileControllerTest(BaseEntityTestHelper<User> testHelper, BaseToTestHelper<UserTo, User> toTestHelper, UserService service) {
        super(ProfileController.URL);
        this.testHelper = testHelper;
        this.toTestHelper = toTestHelper;
        this.service = service;
    }

    @Test
    void register(@Autowired UserRepository repository) throws Exception {
        var newUser = testHelper.newEntity();
        ResultActions actions = perform(doPost("registration").userAsFormData(newUser))
                .andDo(print())
                .andExpect(status().isCreated());
        newUser.setId(getCreatedResourceId(actions.andReturn()));
        actions.andExpect(toTestHelper.forAuth(newUser).contentJson(newUser));

        var created = repository.findByLogin(newUser.getLogin());
        newUser.setId(created.getId());
        testHelper.assertMatch(created, newUser);
    }

    @Test
    void update() throws Exception {
        var userToUpdate = testHelper.editedEntity(USER);
        perform(doPost().userAsFormData(userToUpdate).auth(USER))
                .andDo(print())
                .andExpect(status().isNoContent());
        testHelper.assertMatch(service.findAll(), DEALER, userToUpdate);
    }

    @Test
    void updateWithoutId() throws Exception {
        var userToUpdate = testHelper.newEntity();
        perform(doPost().userAsFormData(userToUpdate).auth(USER))
                .andDo(print())
                .andExpect(status().isNoContent());
        userToUpdate.setId(USER.getId());
        testHelper.assertMatch(service.findAll(), DEALER, userToUpdate);
    }

    @Test
    void delete() throws Exception {
        perform(doDelete().auth(USER))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> service.find(USER.getId()));
        var thrown = assertThrows(NotFoundException.class, SecurityHelper::get);
        assertEquals("No authorized user found", thrown.getMessage());
    }
}
