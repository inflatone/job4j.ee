package ru.job4j.auto.repository;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.auto.model.Post;
import ru.job4j.auto.model.User;

import javax.persistence.EntityNotFoundException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import java.util.List;
import java.util.Map;

import static org.hibernate.graph.GraphSemantic.LOAD;

@Repository
public class PostRepository extends BaseEntityRepository<Post> {
    public PostRepository() {
        super(Post.class);
    }

    @Override
    protected TypedQuery<Post> prepareToExecute(TypedQuery<Post> query) {
        return query.setHint(LOAD.getJpaHintName(), em.createEntityGraph(Post.POST_WITH_CAR));
    }

    /**
     * All find all user queries gonna be sorted reversed by date posted
     *
     * @param cb criteria builder
     * @param c  criteria query
     * @return user order defined
     */
    @Override
    protected Order orderedBy(CriteriaBuilder cb, CriteriaQuery<Post> c) {
        return cb.desc(c.from(entityClass).get("posted"));
    }

    @Transactional
    public Post save(Post post, int profileId) {
        post.setUser(em.getReference(User.class, profileId));
        return super.save(post);
    }

    @Transactional
    public void delete(int id, int profileId) {
        if (createNamedQuery(Post.DELETE, Map.of("id", id, "profileId", profileId))
                .executeUpdate() == 0) {
            throw new EntityNotFoundException("Unable to find post with id=" + id);
        }
    }

    public List<Post> findAll(int profileId) {
        return prepareToExecute(createTypedNamedQuery(Post.FIND_ALL, Map.of("profileId", profileId)))
                .getResultList();
    }

    public Post find(int id, int profileId) {
        return createTypedNamedQuery(Post.FIND, Map.of("id", id, "profileId", profileId))
                .getResultStream()
                .findFirst()
                .orElse(null);
    }
}
