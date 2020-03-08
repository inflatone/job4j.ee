package ru.job4j.auto.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.auto.model.Car;
import ru.job4j.auto.model.Post;
import ru.job4j.auto.repository.PostRepository;

import java.util.List;

import static ru.job4j.auto.util.ValidationHelper.checkNotFoundEntityWithId;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository repository;

    public Post find(int id) {
        return checkNotFoundEntityWithId(repository.find(id), id);
    }

    public Post find(int id, int profileId) {
        return checkNotFoundEntityWithId(repository.find(id, profileId), id);
    }

    public List<Post> findAll() {
        return repository.findAll();
    }

    public List<Post> findAll(int profileId) {
        return repository.findAll(profileId);
    }

    public Post create(Post post, int profileId) {
        return repository.save(post, profileId);
    }

    @Transactional
    public void update(Post post, int profileId) {
        var id = post.getId();
        var persisted = checkNotFoundEntityWithId(find(id, profileId), id);
        persisted.setTitle(post.getTitle());
        persisted.setMessage(post.getMessage());
        persisted.setPrice(post.getPrice());
        persisted.setCar(post.getCar());
        repository.save(persisted, profileId);
    }

    @Transactional
    public void complete(int id, int profileId, boolean complete) {
        Post persisted = checkNotFoundEntityWithId(find(id, profileId), id);
        persisted.setCompleted(complete);
    }

    public void delete(int id, int profileId) {
        repository.delete(id, profileId);
    }
}
