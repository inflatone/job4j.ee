package ru.job4j.auto.repository;

import ru.job4j.auto.model.Post;
import ru.job4j.auto.repository.env.JpaManager;

import javax.inject.Inject;

public class PostRepository extends BaseEntityRepository<Post> {
    @Inject
    public PostRepository(JpaManager jm) {
        super(Post.class, jm);
    }
}
