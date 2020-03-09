package ru.job4j.ee.store.service;

import com.google.inject.Inject;
import com.google.inject.Module;
import org.junit.jupiter.api.Test;
import ru.job4j.ee.store.AssertionUtil;
import ru.job4j.ee.store.model.User;
import ru.job4j.ee.store.util.exception.NotFoundException;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.*;
import static ru.job4j.ee.store.AssertionUtil.*;

public abstract class UserServiceTest extends AbstractServiceTest {
    public UserServiceTest(Module repositoryModule) {
        super(repositoryModule);
    }

    @Inject
    private UserService userService;

    @Test
    void create() {
        var user = new User(UNREGISTERED);
        var usersBefore = userService.findAll();

        userService.create(user);

        var users = userService.findAll();
        users.removeAll(usersBefore);

        assertEquals(1, users.size());
        AssertionUtil.assertMatch(user, users.get(0));
    }

    @Test
    void createDuplicateLogin() {
        var user = new User(UNREGISTERED);
        userService.create(user);
        user.setId(null); // nullification to try save() again
        AssertionUtil.assertThrows(SQLException.class, () -> userService.create(user));
    }

    @Test
    void find() {
        assertMatch(userService.find(USER.getId()), USER);
    }

    @Test
    void findNotFound() {
        assertThrows(NotFoundException.class, () -> userService.find(0));
    }

    @Test
    void update() {
        var updatedModel = new User(USER);
        updatedModel.setName("UpdatedName");
        updatedModel.setPassword("UpdatedPassword");
        updatedModel.setCity(SAINT_PETERSBURG);

        userService.update(updatedModel);

        var persisted = userService.find(USER.getId());
        AssertionUtil.assertMatch(persisted, updatedModel);
    }

    @Test
    void updateNotFound() {
        var user = new User(UNREGISTERED);
        user.setId(0);
        assertThrows(NotFoundException.class, () -> userService.update(user));
    }

    @Test
    void delete() {
        userService.delete(USER.getId());
        assertThrows(NotFoundException.class, () -> userService.find(USER.getId()));
    }


    @Test
    void deleteNotFound() {
        assertThrows(NotFoundException.class, () -> userService.delete(0));
    }

    @Test
    void enable() {
        userService.enable(USER.getId(), false);
        assertFalse(userService.find(USER.getId()).isEnabled());

        userService.enable(USER.getId(), true);
        assertTrue(userService.find(USER.getId()).isEnabled());
    }

    @Test
    void enableNotFound() {
        assertThrows(NotFoundException.class, () -> userService.enable(0, false));
    }
}
