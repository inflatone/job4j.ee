package ru.job4j.auto.repository;

import ru.job4j.auto.model.Post;
import ru.job4j.auto.repository.env.JpaManager;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;

import static org.hibernate.graph.GraphSemantic.LOAD;

public class PostRepository extends BaseEntityRepository<Post> {
    @Inject
    public PostRepository(JpaManager jm) {
        super(Post.class, jm);
    }

    @Override
    List<Post> findAll(EntityManager em) {
        CriteriaQuery<Post> c = em.getCriteriaBuilder().createQuery(entityClass);
        c.select(c.from(entityClass));
        return em.createQuery(c)
                .setHint(LOAD.getJpaHintName(), em.createEntityGraph(Post.POST_WITH_CAR))
                .getResultList();
    }
}
