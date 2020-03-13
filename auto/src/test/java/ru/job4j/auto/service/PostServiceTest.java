package ru.job4j.auto.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.job4j.auto.BaseEntityTestHelper;
import ru.job4j.auto.model.Post;
import ru.job4j.auto.util.exception.NotFoundException;

import javax.persistence.EntityNotFoundException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static ru.job4j.auto.TestModelData.*;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
class PostServiceTest extends AbstractServiceTest {
    private final PostService service;

    private final BaseEntityTestHelper<Post> postEntityTestHelper;

    @Test
    void findAll() {
        List<Post> posts = service.findAll();
        postEntityTestHelper.assertMatch(posts, POST_MAZDA6, POST_BMW, POST_MAZDA3);
    }

    @Test
    void findAllByProfileId() {
        List<Post> userPosts = service.findAll(USER.getId());
        postEntityTestHelper.assertMatch(userPosts, POST_MAZDA6, POST_BMW);
    }

    @Test
    void complete() {
        service.complete(POST_BMW.getId(), USER.getId(), true);
        assertTrue(service.find(POST_BMW.getId(), USER.getId()).isCompleted());
        service.complete(POST_BMW.getId(), USER.getId(), false);
        assertFalse(service.find(POST_BMW.getId(), USER.getId()).isCompleted());
    }

    @Test
    void delete() {
        service.delete(POST_BMW.getId(), USER.getId());
        var thrown = assertThrows(NotFoundException.class, () -> service.find(POST_BMW.getId(), USER.getId()));
        assertEquals("Not found entity with id=" + POST_BMW.getId(), thrown.getMessage());
    }

    @Test
    void deleteNotOwn() {
        var thrown = assertThrows(EntityNotFoundException.class, () -> service.delete(POST_BMW.getId(), DEALER.getId()));
        assertEquals("Unable to find post with id=" + POST_BMW.getId(), thrown.getMessage());
    }
}
