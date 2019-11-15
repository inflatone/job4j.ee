package ru.job4j.auto.repository;

import com.google.inject.Inject;
import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import org.junit.jupiter.api.Test;
import ru.job4j.auto.inject.ExtendedRepositoryModule;
import ru.job4j.auto.model.User;

import javax.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertNull;
import static ru.job4j.auto.EntityTestHelpers.*;
import static ru.job4j.auto.TestModelData.*;

@IncludeModule(ExtendedRepositoryModule.class)
class UserRepositoryTest extends AbstractBaseRepositoryTest {
    @Inject
    private UserRepository repository;

    @Test
    void create() {
        var newUser = USER_TEST_HELPER.newEntity();
        User saved = repository.save(USER_TEST_HELPER.copy(newUser));
        var newId = saved.getId();
        newUser.setId(newId);
        USER_TEST_HELPER.assertMatch(saved, newUser);
        USER_TEST_HELPER.assertMatch(repository.find(newId), newUser);
    }

    @Test
    void update() {
        var userToUpdate = USER_TEST_HELPER.editedEntity(USER);
        User saved = repository.save(USER_TEST_HELPER.copy(userToUpdate));

        USER_TEST_HELPER.assertMatch(saved, userToUpdate);
        USER_TEST_HELPER.assertMatch(repository.find(USER.getId()), userToUpdate);
    }

    @Test
    void delete() {
        repository.delete(USER.getId());
        assertNull(repository.find(USER.getId()));
    }

    @Test
    void deleteNotFound() {
        validateRootCause(EntityNotFoundException.class, () -> repository.delete(0));
    }

    @Test
    void find() {
        var user = repository.find(DEALER.getId());
        USER_TEST_HELPER.assertMatch(user, DEALER);
    }

    @Test
    void findByLogin() {
        var user = repository.findByLogin(DEALER.getLogin());
        USER_TEST_HELPER.assertMatch(user, DEALER);
    }

    @Test
    void findAll() {
        var users = repository.findAll();
        USER_TEST_HELPER.assertMatch(users, USERS);
    }

    @Test
    void findWithPosts() {
        var user = repository.findWithPosts(USER.getId());
        USER_TEST_HELPER.assertMatch(user, USER);
        POST_TEST_HELPER.assertMatch(user.getPosts(), POST_MAZDA6, POST_BMW);
    }
}
