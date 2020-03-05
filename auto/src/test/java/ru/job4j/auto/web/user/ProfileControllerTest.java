package ru.job4j.auto.web.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.job4j.auto.BaseEntityTestHelper;
import ru.job4j.auto.model.User;
import ru.job4j.auto.repository.UserRepository;
import ru.job4j.auto.service.UserService;
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

    private final UserService service;

    @Autowired
    ProfileControllerTest(BaseEntityTestHelper<User> testHelper, UserService service) {
        super(ProfileController.URL);
        this.testHelper = testHelper;
        this.service = service;
    }

    @BeforeEach
    void setUp() {
        SecurityHelper.setAuth(USER);
    }

    @Test
    void find() throws Exception {
        perform(doGet())
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(contentTypeIsJson())
                .andExpect(testHelper.contentJson(USER));
    }

    @Test
    void register(@Autowired UserRepository repository) throws Exception {
        var newUser = testHelper.newEntity();
        perform(doPost("registration").userAsFormData(newUser))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn();
        var created = repository.findByLogin(newUser.getLogin());
        newUser.setId(created.getId());
        testHelper.assertMatch(created, newUser);
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
    void delete() throws Exception {
        perform(doDelete())
                .andDo(print())
                .andExpect(status().isNoContent());
        assertThrows(NotFoundException.class, () -> service.find(USER.getId()));
        var thrown = assertThrows(NotFoundException.class, SecurityHelper::get);
        assertEquals("No authorized user found", thrown.getMessage());
    }
}
