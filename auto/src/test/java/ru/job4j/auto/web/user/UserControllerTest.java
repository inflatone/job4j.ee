package ru.job4j.auto.web.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.job4j.auto.config.helper.BaseToTestHelper;
import ru.job4j.auto.model.User;
import ru.job4j.auto.service.UserService;
import ru.job4j.auto.to.UserTo;
import ru.job4j.auto.web.AbstractControllerTest;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.job4j.auto.TestModelData.DEALER;
import static ru.job4j.auto.TestModelData.USER;

public class UserControllerTest extends AbstractControllerTest {
    private final BaseToTestHelper<UserTo, User> testHelper;

    private final UserService service;

    @Autowired
    public UserControllerTest(BaseToTestHelper<UserTo, User> testHelper, UserService service) {
        super(UserController.URL);
        this.testHelper = testHelper;
        this.service = service;
    }

    @Test
    void find() throws Exception {
        perform(doGet(DEALER.getId()).auth(USER))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeIsJson())
                .andExpect(testHelper.forAuth(USER).contentJson(DEALER));
    }

    @Test
    void findSelf() throws Exception {
        perform(doGet(USER.getId()).auth(USER))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeIsJson())
                .andExpect(testHelper.forAuth(USER).contentJson(USER));
    }

    @Test
    void findSelfAsAdmin() throws Exception {
        perform(doGet(DEALER.getId()).auth(DEALER))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeIsJson())
                .andExpect(testHelper.forAuth(DEALER).contentJson(DEALER));
    }

    @Test
    void findAsAdmin() throws Exception {
        perform(doGet(USER.getId()).auth(DEALER))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeIsJson())
                .andExpect(testHelper.forAuth(DEALER).contentJson(USER));
    }

    @Test
    void findAll() throws Exception {
        perform(doGet().auth(USER))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeIsJson())
                .andExpect(testHelper.forAuth(USER).contentJson(DEALER, USER));
    }

    @Test
    void findAllAsAdmin() throws Exception {
        perform(doGet().auth(DEALER))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeIsJson())
                .andExpect(testHelper.forAuth(DEALER).contentJson(DEALER, USER));
    }

    @Test
    void findFiltered() throws Exception {

    }
}
