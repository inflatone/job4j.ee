package ru.job4j.ee.store.service;

import com.google.inject.Inject;
import com.google.inject.Module;
import org.junit.jupiter.api.Test;
import ru.job4j.ee.store.AssertionUtil;
import ru.job4j.ee.store.model.User;

import static ru.job4j.ee.store.AssertionUtil.*;

public abstract class SecurityServiceTest extends AbstractServiceTest {
    public SecurityServiceTest(Module repositoryModule) {
        super(repositoryModule);
    }

    @Inject
    private SecurityService securityService;

    @Test
    void find() {
        var retrieved = findByLoginAndPassword(USER.getLogin(), USER.getPassword());
        assertMatch(USER, retrieved);
    }

    @Test
    void findNotExisted() {
        assertThrows(SecurityException.class, "User with the given login is not registered",
                () -> findByLoginAndPassword("notExisted", "password"));
    }

    @Test
    void findIncorrectPassword() {
        assertThrows(SecurityException.class, "Wrong credentials",
                () -> findByLoginAndPassword(USER.getLogin(), "incorrect"));
    }

    @Test
    void findBanned() {
        assertThrows(SecurityException.class, "User is banned",
                () -> findByLoginAndPassword(BANNED.getLogin(), BANNED.getPassword()));
    }

    private User findByLoginAndPassword(String login, String password) {
        return securityService.find(AssertionUtil.createUserModel(login, password, null, null));
    }
}