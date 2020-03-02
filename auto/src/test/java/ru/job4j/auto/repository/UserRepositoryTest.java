package ru.job4j.auto.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import ru.job4j.auto.EntityTestHelper;
import ru.job4j.auto.model.Post;
import ru.job4j.auto.model.User;

import javax.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertNull;
import static ru.job4j.auto.EntityTestHelpers.validateRootCause;
import static ru.job4j.auto.TestModelData.*;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = {"classpath:db/import/data.sql"}, config = @SqlConfig(encoding = "UTF-8"))
class UserRepositoryTest extends AbstractBaseRepositoryTest {
    private final UserRepository repository;

    private final EntityTestHelper<User> testHelper;

    @Test
    void create() {
        var newUser = testHelper.newEntity();
        User saved = repository.save(testHelper.copy(newUser));
        var newId = saved.getId();
        newUser.setId(newId);
        testHelper.assertMatch(saved, newUser);
        testHelper.assertMatch(repository.find(newId), newUser);
    }

    @Test
    void update() {
        var userToUpdate = testHelper.editedEntity(USER);
        User saved = repository.save(testHelper.copy(userToUpdate));

        testHelper.assertMatch(saved, userToUpdate);
        testHelper.assertMatch(repository.find(USER.getId()), userToUpdate);
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
        testHelper.assertMatch(user, DEALER);
    }

    @Test
    void findByLogin() {
        var user = repository.findByLogin(DEALER.getLogin());
        testHelper.assertMatch(user, DEALER);
    }

    @Test
    void findAll() {
        var users = repository.findAll();
        testHelper.assertMatch(users, USERS);
    }

    @Test
    void findWithPosts(@Autowired EntityTestHelper<Post> postEntityTestHelper) {
        var user = repository.findWithPosts(USER.getId());
        testHelper.assertMatch(user, USER);
        postEntityTestHelper.assertMatch(user.getPosts(), POST_MAZDA6, POST_BMW);
    }
}
