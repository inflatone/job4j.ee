package ru.job4j.auto.web.post;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.job4j.auto.model.Post;
import ru.job4j.auto.service.PostService;

import java.util.List;

import static ru.job4j.auto.util.ValidationHelper.assureIdConsistent;

@Slf4j
@RequiredArgsConstructor
public class AbstractPostController {
    private final PostService service;

    protected Post find(int id, int profileId) {
        log.info("Find {} for user with id={}", id, profileId);
        return service.find(id, profileId);
    }

    protected List<Post> findAll() {
        log.info("Find all posts of all users");
        return service.findAll();
    }

    protected List<Post> findAll(int profileId) {
        log.info("Find all posts for user with id={}", profileId);
        return service.findAll(profileId);
    }

    protected Post create(Post post, int profileId) {
        log.info("Create {} for user with id={}", post, profileId);
        return service.create(post, profileId);
    }

    protected void update(Post post, int id, int profileId) {
        log.info("Update {} with id={} for user with id={}", post, id, profileId);
        assureIdConsistent(post, id);
        service.update(post, profileId);
    }

    protected void complete(int id, int profileId, boolean completed) {
        service.complete(id, profileId, completed);
    }

    protected void delete(int id, int profileId) {
        log.info("Delete post with id={} for user with id={}", id, profileId);
        service.delete(id, profileId);
    }
}
