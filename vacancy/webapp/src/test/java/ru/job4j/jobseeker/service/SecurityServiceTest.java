package ru.job4j.jobseeker.service;

import org.junit.jupiter.api.Test;
import ru.job4j.jobseeker.model.User;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.job4j.jobseeker.TestHelper.*;

public class SecurityServiceTest extends AbstractServiceTest {
    @Inject
    private SecurityService service;

    @Test
    void find() {
        User retrieved = service.find(USER.getLogin(), USER.getPassword());
        USER_MATCHERS.assertMatch(retrieved, USER);
    }

    @Test
    void findNotExisted() {
        var thrown = assertThrows(SecurityException.class, () -> service.find("notExisted", "notExisted"));
        assertEquals("User with the given login is not registered", thrown.getMessage());
    }

    @Test
    void findIncorrectPassword() {
        var thrown = assertThrows(SecurityException.class, () -> service.find(ADMIN.getLogin(), "incorrect"));
        assertEquals("Wrong credentials", thrown.getMessage());
    }
}
