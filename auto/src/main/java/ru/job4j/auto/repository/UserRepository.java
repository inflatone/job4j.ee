package ru.job4j.auto.repository;

import ru.job4j.auto.model.User;
import ru.job4j.auto.repository.env.JpaManager;

import javax.inject.Inject;
import java.util.Map;

import static org.hibernate.graph.GraphSemantic.FETCH;

public class UserRepository extends BaseEntityRepository<User> {
    @Inject
    public UserRepository(JpaManager jm) {
        super(User.class, jm);
    }

    /**
     * Finds a user entity associated with the given id, also fetches all their posts
     *
     * @param id id
     * @return user entity
     */
    public User findWithPosts(int id) {
        return jm.transactionalRetrieve(em -> em.find(User.class, id, Map.of(FETCH.getJpaHintName(),
                em.createEntityGraph(User.USER_WITH_POSTS))));
    }

    /**
     * Finds a user entity associated with the given login
     *
     * @param login login
     * @return user entity
     */
    public User findByLogin(String login) {
        return jm.transactionalRetrieve(em ->
                createTypedNamedQuery(em, User.BY_LOGIN, Map.of("login", login))
                        .getResultStream()
                        .findFirst()
                        .orElse(null)
        );
    }
}
