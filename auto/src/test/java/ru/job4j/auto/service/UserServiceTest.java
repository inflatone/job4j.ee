package ru.job4j.auto.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.job4j.auto.BaseEntityTestHelper;
import ru.job4j.auto.model.Post;
import ru.job4j.auto.model.User;
import ru.job4j.auto.util.exception.NotFoundException;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static ru.job4j.auto.TestModelData.*;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceTest extends AbstractServiceTest {
    private final UserService service;

    private final BaseEntityTestHelper<User> testHelper;

    @Test
    void enable() {
        service.enable(USER.getId(), false);
        assertFalse(service.find(USER.getId()).isEnabled());

        service.enable(USER.getId(), true);
        assertTrue(service.find(USER.getId()).isEnabled());
    }

    @Test
    void enableNotFound() {
        var thrown = assertThrows(NotFoundException.class, () -> service.enable(0, true));
        assertEquals("Not found entity with id=0", thrown.getMessage());
    }

    @Test
    void find() {
        testHelper.assertMatch(service.find(USER.getId()), USER);
    }

    @Test
    void findNotFound() {
        var thrown = assertThrows(NotFoundException.class, () -> service.find(0));
        assertEquals("Not found entity with id=0", thrown.getMessage());
    }

    @Test
    void findWithPosts(@Autowired BaseEntityTestHelper<Post> postEntityTestHelper) {
        User persisted = service.findWithPosts(USER.getId());
        testHelper.assertMatch(persisted, USER);
        postEntityTestHelper.assertMatch(persisted.getPosts(), POST_MAZDA6, POST_BMW);
    }

    @Test
    void updateWithNoPassword() {
        User userToUpdate = testHelper.editedEntity(USER);
        userToUpdate.setPassword("");
        service.update(userToUpdate);

        User persisted = service.find(USER.getId());
        assertEquals(USER.getPassword(), persisted.getPassword());

        userToUpdate.setPassword(USER.getPassword());
        testHelper.assertMatch(persisted, userToUpdate);
    }

    @Test
    void updateWithNoUpdatableFields() {
        User userToUpdate = testHelper.editedEntity(USER);
        userToUpdate.setEnabled(false);
        userToUpdate.setRegistered(Instant.MAX);
        service.update(userToUpdate);

        User persisted = service.find(USER.getId());
        assertTrue(persisted.isEnabled());
        assertNotEquals(Instant.MAX, persisted.getRegistered());

        userToUpdate.setEnabled(persisted.isEnabled());
        testHelper.assertMatch(persisted, userToUpdate);
    }
}
