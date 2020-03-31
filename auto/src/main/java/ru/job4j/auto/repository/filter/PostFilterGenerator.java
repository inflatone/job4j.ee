package ru.job4j.auto.repository.filter;

import com.google.common.collect.Maps;
import org.springframework.stereotype.Component;
import ru.job4j.auto.model.Post;
import ru.job4j.auto.to.filter.BaseFilterTo.Range;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

@Component
public class PostFilterGenerator implements FilterGenerator<Post> {
    private final Map<String, Function<Object, BiFunction<CriteriaBuilder, Root<Post>, Predicate>>> generators = Maps.newHashMap();

    @PostConstruct
    protected void init() {
        createEqualsStatement("user", "user", "id");
        createEqualsStatement("vendor", "car", "vendor", "id");
        createEqualsStatement("body", "car", "body", "id");
        createEqualsStatement("engine", "car", "engine", "id");
        createEqualsStatement("transmission", "car", "transmission", "id");

        createStatement("image", (cb, r) -> cb.isNotNull(r.get("image").get("id")));
        createStatement("actual", (cb, r) -> cb.isFalse(r.get("completed")));

        createBetweenStatement("price", "price");
        createBetweenStatement("posted", "posted");
        createBetweenStatement("year", "car", "year");
    }

    @Override
    public Predicate generate(String key, Object value, CriteriaBuilder cb, Root<Post> root) {
        return generators.get(key).apply(value).apply(cb, root);
    }

    private void createEqualsStatement(String key, String propertyRoot, String... propertyGraph) {
        generators.put(key, v -> (cb, r) -> cb.equal(getPath(r, propertyRoot, propertyGraph), v));
    }


    private void createStatement(String property, BiFunction<CriteriaBuilder, Root<Post>, Predicate> builder) {
        generators.put(property, v -> builder);
    }

    @SuppressWarnings("unchecked")
    private <E extends Comparable<? super E>> void createBetweenStatement(String key, String propertyRoot, String... propertyGraph) {
        Function<Object, BiFunction<CriteriaBuilder, Root<Post>, Predicate>> rangeExpression =
                v -> (cb, r) -> cb.between(getPath(r, propertyRoot, propertyGraph), ((Range<E>) v).getMin(), ((Range<E>) v).getMax());
        generators.put(key, rangeExpression);
    }

    private static <E> Path<E> getPath(Root<Post> root, String rootProperty, String... propertyGraph) {
        Path<E> result = root.get(rootProperty);
        for (var el : propertyGraph) {
            result = result.get(el);
        }
        return result;
    }
}
