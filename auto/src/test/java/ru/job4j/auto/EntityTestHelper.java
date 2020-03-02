package ru.job4j.auto;

import one.util.streamex.StreamEx;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class EntityTestHelper<T> {
    private final boolean checkRecursively;
    private final String[] fieldsToIgnore;

    protected EntityTestHelper(boolean checkRecursively, String... fieldsToIgnore) {
        this.checkRecursively = checkRecursively;
        this.fieldsToIgnore = fieldsToIgnore;
    }

    public void assertMatch(T actual, T expected, String... additionalFieldsToIgnore) {
        var fieldsToIgnore = additionalFieldsToIgnore.length == 0 ? this.fieldsToIgnore
                : StreamEx.of(this.fieldsToIgnore, additionalFieldsToIgnore).flatMap(StreamEx::of).toArray(String.class);
        if (checkRecursively) {
            assertThat(actual).usingRecursiveComparison().ignoringFields(fieldsToIgnore).isEqualTo(expected);
        } else {
            assertThat(actual).isEqualToIgnoringGivenFields(expected, fieldsToIgnore);
        }
    }

    public void assertMatch(Iterable<T> actual, Iterable<T> expected) {
        if (checkRecursively) {
            assertThat(actual).usingRecursiveFieldByFieldElementComparator()
                    .usingElementComparatorIgnoringFields(fieldsToIgnore)
                    .isEqualTo(expected);
        } else {
            assertThat(actual).usingElementComparatorIgnoringFields(fieldsToIgnore).isEqualTo(expected);
        }
    }

    @SafeVarargs
    public final void assertMatch(Iterable<T> actual, T... expected) {
        assertMatch(actual, List.of(expected));
    }

    public T nullableCopy(T entity) {
        return entity == null ? null : doCopy(entity);
    }

    public T copy(T entity) {
        return doCopy(Objects.requireNonNull(entity, entity + " must be not null"));
    }

    protected abstract T doCopy(T entity);

    public abstract T newEntity();

    public abstract T editedEntity(T t);
}
