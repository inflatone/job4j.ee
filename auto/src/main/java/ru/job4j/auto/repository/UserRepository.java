package ru.job4j.auto.repository;

import ru.job4j.auto.model.User;
import ru.job4j.auto.repository.env.JpaManager;

import javax.inject.Inject;
import java.util.Map;

public class UserRepository extends BaseEntityRepository<User> {
    @Inject
    public UserRepository(JpaManager jm) {
        super(User.class, jm);
    }

    public User findWithPosts(int id) {
        return jm.transactionalRetrieve(em ->
                createTypedQuery(em, "SELECT u FROM User u LEFT JOIN FETCH u.posts WHERE u.id=:id", Map.of("id", id))
                        .getSingleResult());
    }

    public User findByLogin(String login) {
        return jm.transactionalRetrieve(em ->
                createTypedQuery(em, "SELECT u FROM User u WHERE u.login=:login", Map.of("login", login))
                        .getResultStream()
                        .findFirst()
                        .orElse(null));
    }
}
