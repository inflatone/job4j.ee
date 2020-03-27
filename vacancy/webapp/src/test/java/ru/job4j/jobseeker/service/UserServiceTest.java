package ru.job4j.jobseeker.service;

import org.hsqldb.HsqlException;
import org.junit.jupiter.api.Test;
import ru.job4j.jobseeker.TestHelper;
import ru.job4j.jobseeker.exeption.ApplicationException;
import ru.job4j.jobseeker.model.Role;
import ru.job4j.jobseeker.model.User;

import javax.inject.Inject;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.job4j.jobseeker.TestHelper.*;

class UserServiceTest extends AbstractServiceTest {
    @Inject
    private UserService service;

    @Test
    void find() {
        User user = service.find(USER.getId());
        USER_MATCHERS.assertMatch(user, USER);
    }

    @Test
    void findNotFound() {
        var thrown = assertThrows(ApplicationException.class, () -> service.find(0));
        assertEquals("Not found entity with id=0", thrown.getMessage());
    }

    @Test
    void create() {
        User newUser = TestHelper.createNewUser();
        User created = service.create(newUser);
        newUser.setId(created.getId());
        USER_MATCHERS.assertMatch(created, newUser);

        List<User> users = service.findAll();
        USER_MATCHERS.assertMatch(users, ADMIN, created, USER);
    }

    @Test
    void createDuplicateLogin() {
        var duplicate = new User(USER);
        duplicate.setId(null);
        var thrown = assertRootThrows(HsqlException.class, () -> service.create(duplicate));
        assertTrue(thrown.getMessage().contains("constraint violation"));
    }

    @Test
    void update() {
        var updatedModel = new User(USER);
        updatedModel.setLogin("UpdatedName");
        updatedModel.setPassword("UpdatedPassword");
        updatedModel.setRole(Role.ADMIN);

        service.update(updatedModel);

        var persisted = service.find(USER.getId());
        USER_MATCHERS.assertMatch(persisted, updatedModel);
    }

    @Test
    void updateNotFound() {
        User user = TestHelper.createNewUser();
        user.setId(0);

        var thrown = assertThrows(ApplicationException.class, () -> service.update(user));
        assertEquals("Not found entity with id=0", thrown.getMessage());
    }

    @Test
    void delete() {
        service.delete(USER.getId());
        var thrown = assertThrows(ApplicationException.class, () -> service.find(USER.getId()));
        assertEquals("Not found entity with id=" + USER.getId(), thrown.getMessage());
    }


    @Test
    void deleteNotFound() {
        var thrown = assertThrows(ApplicationException.class, () -> service.delete(0));
        assertEquals("Not found entity with id=0", thrown.getMessage());
    }
}
