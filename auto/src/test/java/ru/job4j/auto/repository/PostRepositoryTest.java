package ru.job4j.auto.repository;

import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import org.junit.jupiter.api.Test;
import ru.job4j.auto.inject.ExtendedRepositoryModule;
import ru.job4j.auto.model.Post;

import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertNull;
import static ru.job4j.auto.EntityTestHelpers.POST_TEST_HELPER;
import static ru.job4j.auto.EntityTestHelpers.validateRootCause;
import static ru.job4j.auto.TestModelData.POSTS;
import static ru.job4j.auto.TestModelData.POST_BMW;

@IncludeModule(ExtendedRepositoryModule.class)
class PostRepositoryTest extends AbstractBaseRepositoryTest {
    @Inject
    private PostRepository repository;

    @Test
    void create() {
        var newPost = POST_TEST_HELPER.newEntity();
        Post saved = repository.save(POST_TEST_HELPER.copy(newPost));
        var newId = saved.getId();

        newPost.setId(newId);
        newPost.getCar().setId(saved.getCar().getId());

        POST_TEST_HELPER.assertMatch(saved, newPost);
        Post persisted = repository.find(newId);
        POST_TEST_HELPER.assertMatch(persisted, newPost);
    }

    @Test
    void update() {
        var postToUpdate = POST_TEST_HELPER.copy(POST_BMW);
        Post saved = repository.save(POST_TEST_HELPER.copy(postToUpdate));

        POST_TEST_HELPER.assertMatch(saved, postToUpdate);
        POST_TEST_HELPER.assertMatch(repository.find(POST_BMW.getId()), postToUpdate);
    }

    @Test
    void delete() {
        repository.delete(POST_BMW.getId());
        assertNull(repository.find(POST_BMW.getId()));
    }

    @Test
    void deleteNotFound() {
        validateRootCause(EntityNotFoundException.class, () -> repository.delete(0));
    }

    @Test
    void find() {
        var post = repository.find(POST_BMW.getId());
        POST_TEST_HELPER.assertMatch(post, POST_BMW);
    }

    @Test
    void findAll() {
        var posts = repository.findAll();
        POST_TEST_HELPER.assertMatch(posts, POSTS);
    }
}