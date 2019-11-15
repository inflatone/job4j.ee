package ru.job4j.auto.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.job4j.auto.model.BaseEntity;
import ru.job4j.auto.repository.env.JpaManager;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseEntityRepository<T extends BaseEntity> {
    final Class<T> entityClass;

    final JpaManager jm;

    public T save(T entity) {
        return jm.transactionalRetrieve(em -> saveOrUpdate(em, entity));
    }

    public void delete(int id) {
        jm.transactionalExecute(em -> delete(em, id));
    }

    public T find(int id) {
        return jm.transactionalRetrieve(em -> find(em, id));
    }

    public List<T> findAll() {
        return jm.transactionalRetrieve(this::findAll);
    }

    T saveOrUpdate(EntityManager em, T entity) {
        if (!entity.isNew()) {
            return em.merge(entity);
        }
        em.persist(entity);
        return entity;
    }

    void delete(EntityManager em, int id) {
        var entity = em.getReference(entityClass, id);
        em.remove(entity);
    }

    T find(EntityManager em, int id) {
        return em.find(entityClass, id);
    }

    List<T> findAll(EntityManager em) {
        CriteriaQuery<T> c = em.getCriteriaBuilder().createQuery(entityClass);
        c.select(c.from(entityClass));
        return em.createQuery(c).getResultList();
    }

    TypedQuery<T> createTypedNamedQuery(EntityManager em, String name, Map<String, Object> params) {
        return createQuery(() -> em.createNamedQuery(name, entityClass), params);
    }

    Query createNamedQuery(EntityManager em, String name, Map<String, Object> params) {
        return createQuery(() -> em.createNamedQuery(name), params);
    }

    TypedQuery<T> createTypedQuery(EntityManager em, String jql, Map<String, Object> params) {
        return createQuery(() -> em.createQuery(jql, entityClass), params);
    }

    private <Q extends Query> Q createQuery(Supplier<Q> queryFactory, Map<String, Object> params) {
        Q result = queryFactory.get();
        params.forEach(result::setParameter);
        return result;
    }
}