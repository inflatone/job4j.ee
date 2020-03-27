package ru.job4j.jobseeker;

import ru.job4j.jobseeker.web.json.JsonHelper;

import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;
import static ru.job4j.vacancy.util.TimeUtil.toZonedDateTime;

public class TestMatchers<T> {
    private final Class<T> clazz;
    private final String[] fieldsToIgnore;

    public static <T> TestMatchers<T> useFieldsComparator(Class<T> clazz, String... fieldsToIgnore) {
        return new TestMatchers<>(clazz, fieldsToIgnore);
    }

    private TestMatchers(Class<T> clazz, String... fieldsToIgnore) {
        this.clazz = clazz;
        this.fieldsToIgnore = fieldsToIgnore;
    }

    public void assertMatch(T actual, T expected) {
        assertThat(actual).isEqualToIgnoringGivenFields(expected, fieldsToIgnore);
    }

    @SafeVarargs
    public final void assertMatch(Iterable<T> actual, T... expected) {
        assertMatch(actual, List.of(expected));
    }

    public void assertMatch(Iterable<T> actual, Iterable<T> expected) {
        assertThat(actual).usingElementComparatorIgnoringFields(fieldsToIgnore).isEqualTo(expected);
    }

    public void assertMatchAsJson(String actual, T expected) {
        assertMatch(JsonHelper.fromJson(actual, clazz), expected);
    }

    @SafeVarargs
    public final void assertMatchAsJson(String actual, T... expected) {
        assertMatchAsJson(actual, List.of(expected));
    }

    public void assertMatchAsJson(String actual, List<T> expected) {
        assertMatch(JsonHelper.listFromJson(actual, clazz), expected);
    }

    // https://stackoverflow.com/a/1698926/10375242
    public static void assertDatesAlmostEqual(Date actual, Date expected) {
        assertThat(actual).isCloseTo(expected, within(1000, ChronoUnit.MILLIS).getValue());
    }
}
