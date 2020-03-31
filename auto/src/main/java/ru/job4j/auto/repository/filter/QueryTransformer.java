package ru.job4j.auto.repository.filter;

import lombok.RequiredArgsConstructor;
import one.util.streamex.StreamEx;
import org.springframework.stereotype.Component;
import ru.job4j.auto.to.filter.BaseFilterTo;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class QueryTransformer<E> {
    private final FilterGenerator<E> generator;

    public CriteriaQuery<E> filteringCriteriaQuery(CriteriaBuilder cb, Root<E> root, CriteriaQuery<E> cq, BaseFilterTo filter) {
        Predicate[] predicates = StreamEx.of(
                filter.getEqualValues().entrySet().stream().map(e -> generate(cb, root, e.getKey(), e.getValue())),
                filter.getRangeValues().entrySet().stream().map(e -> generate(cb, root, e.getKey(), e.getValue())),
                filter.getBooleanValues().entrySet().stream().map(e -> generate(cb, root, e.getKey(), e.getValue(), v -> v))
        )
                .flatMap(Function.identity())
                .nonNull()
                .toArray(Predicate[]::new);
        return cq.where(predicates);
    }

    private Predicate generate(CriteriaBuilder cb, Root<E> root, String key, Object value) {
        return generate(cb, root, key, value, v -> true);
    }

    private <T> Predicate generate(CriteriaBuilder cb, Root<E> root, String key, T value, java.util.function.Predicate<T> presenceChecker) {
        return presenceChecker.test(value) ? generator.generate(key, value, cb, root) : null;
    }
}
