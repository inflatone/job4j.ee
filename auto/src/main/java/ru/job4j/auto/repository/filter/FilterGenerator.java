package ru.job4j.auto.repository.filter;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public interface FilterGenerator<E> {
    Predicate generate(String key, Object value, CriteriaBuilder cb, Root<E> root);
}
