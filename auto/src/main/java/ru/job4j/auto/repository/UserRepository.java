package ru.job4j.auto.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.auto.model.User;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import java.util.Map;

import static org.hibernate.graph.GraphSemantic.FETCH;

@Repository
public class UserRepository extends BaseEntityRepository<User> {
    public UserRepository() {
        super(User.class);
    }

    /**
     * Finds a user entity associated with the given id, also fetches all their posts
     *
     * @param id id
     * @return user entity
     */
    public User findWithPosts(int id) {
        return em.find(User.class, id, Map.of(FETCH.getJpaHintName(),
                em.createEntityGraph(User.USER_WITH_POSTS)));
    }

    /**
     * Finds a user entity associated with the given login
     *
     * @param login login
     * @return user entity
     */
    public User findByLogin(String login) {
        return createTypedNamedQuery(User.BY_LOGIN, Map.of("login", login))
                .getResultStream()
                .findFirst()
                .orElse(null);
    }

    /**
     * All find all user queries gonna be sorted by registered date
     *
     * @param cb criteria builder
     * @param c  criteria query
     * @return user order defined
     */
    @Override
    protected Order orderedBy(CriteriaBuilder cb, CriteriaQuery<User> c) {
        return cb.asc(c.from(entityClass).get("registered"));
    }
}
