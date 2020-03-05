package ru.job4j.auto.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import ru.job4j.auto.BaseEntityTestHelper;
import ru.job4j.auto.model.Post;

import javax.persistence.EntityNotFoundException;

import static org.junit.jupiter.api.Assertions.assertNull;
import static ru.job4j.auto.EntityTestHelpers.validateRootCause;
import static ru.job4j.auto.TestModelData.POSTS;
import static ru.job4j.auto.TestModelData.POST_BMW;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Sql(scripts = {"classpath:db/import/data.sql"}, config = @SqlConfig(encoding = "UTF-8"))
class PostRepositoryTest extends AbstractBaseRepositoryTest {
    private final PostRepository repository;

    private final BaseEntityTestHelper<Post> testHelper;

    @Test
    void create() {
        var newPost = testHelper.newEntity();
        Post saved = repository.save(testHelper.copy(newPost));
        var newId = saved.getId();

        newPost.setId(newId);
        newPost.getCar().setId(saved.getCar().getId());

        testHelper.assertMatch(saved, newPost);
        Post persisted = repository.find(newId);
        testHelper.assertMatch(persisted, newPost);
    }

    @Test
    void update() {
        var postToUpdate = testHelper.copy(POST_BMW);
        Post saved = repository.save(testHelper.copy(postToUpdate));

        testHelper.assertMatch(saved, postToUpdate);
        testHelper.assertMatch(repository.find(POST_BMW.getId()), postToUpdate);
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
        testHelper.assertMatch(post, POST_BMW);
    }

    @Test
    void findAll() {
        var posts = repository.findAll();
        testHelper.assertMatch(posts, POSTS);
    }
}