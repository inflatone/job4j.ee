package ru.job4j.auto.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import ru.job4j.auto.model.BaseEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public abstract class BaseEntityRepository<T extends BaseEntity> {
    final Class<T> entityClass;

    @PersistenceContext
    protected EntityManager em;

    @Transactional
    public T save(T entity) {
        if (!entity.isNew()) {
            return em.merge(entity);
        }
        em.persist(entity);
        return entity;
    }

    @Transactional
    public void delete(int id) {
        var entity = em.getReference(entityClass, id);
        em.remove(entity);
    }

    public T find(int id) {
        return em.find(entityClass, id);
    }

    public List<T> findAll() {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> c = cb.createQuery(entityClass);
        c.orderBy(orderedBy(cb, c));
        return prepareToExecute(em.createQuery(c)).getResultList();
    }

    /**
     * Override to set specific found entities order
     *
     * @param cb criteria builder
     * @param c  criteria query
     * @return order defined
     */
    protected Order orderedBy(CriteriaBuilder cb, CriteriaQuery<T> c) {
        return cb.asc(c.from(entityClass).get("id"));
    }

    /**
     * Override to set additional params before a query is to execute
     *
     * @return order defined
     */
    protected TypedQuery<T> prepareToExecute(TypedQuery<T> query) {
        return query;
    }

    /**
     * Creates a typed query by the given name, then sets on it the given params
     *
     * @param name   given name
     * @param params given params
     * @return built typed query
     */
    TypedQuery<T> createTypedNamedQuery(String name, Map<String, Object> params) {
        return createQuery(em -> em.createNamedQuery(name, entityClass), params);
    }

    /**
     * Creates a query by the given name, then sets on it the given params
     *
     * @param name   given name
     * @param params given params
     * @return built query
     */
    Query createNamedQuery(String name, Map<String, Object> params) {
        return createQuery(em -> em.createNamedQuery(name), params);
    }

    /**
     * Creates a typed query by the given query factory, then sets on it the given params
     *
     * @param queryFactory   query factory
     * @param params given params
     * @return built typed query
     */
    private <Q extends Query> Q createQuery(Function<EntityManager, Q> queryFactory, Map<String, Object> params) {
        Q result = queryFactory.apply(em);
        params.forEach(result::setParameter);
        return result;
    }
}