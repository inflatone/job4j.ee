package ru.job4j.auto.config.helper;

import one.util.streamex.StreamEx;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import ru.job4j.auto.web.converter.JsonHelper;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

public abstract class AbstractEntityTestHelper<T> {
    protected final JsonHelper jsonHelper;

    private final Class<T> clazz;

    private final boolean checkRecursively;
    private final String[] fieldsToIgnore;

    protected AbstractEntityTestHelper(JsonHelper jsonHelper, Class<T> clazz, boolean checkRecursively, String... fieldsToIgnore) {
        this.jsonHelper = jsonHelper;
        this.clazz = clazz;
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

    public ResultMatcher contentJson(T expected) {
        return result -> assertMatch(readFromJsonMvcResult(result), expected);
    }

    public ResultMatcher contentJson(Iterable<T> expected) {
        return result -> assertMatch(readListFromJsonMvcResult(result), expected);
    }

    @SafeVarargs
    public final void assertMatch(Iterable<T> actual, T... expected) {
        assertMatch(actual, List.of(expected));
    }

    @SafeVarargs
    public final ResultMatcher contentJson(T... expected) {
        return contentJson(List.of(expected));
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

    public T readFromJsonMvcResult(MvcResult result) throws UnsupportedEncodingException {
        return jsonHelper.fromJson(getContent(result), clazz);
    }

    public List<T> readListFromJsonMvcResult(MvcResult result) throws UnsupportedEncodingException {
        return jsonHelper.listFromJson(getContent(result), clazz);
    }

    protected static String getContent(MvcResult result) throws UnsupportedEncodingException {
        return result.getResponse().getContentAsString();
    }
}
