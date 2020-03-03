package ru.job4j.auto.repository;

import org.springframework.stereotype.Repository;
import ru.job4j.auto.model.Post;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;

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
}
