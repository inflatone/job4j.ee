package ru.job4j.vacancy.jsoup;

import org.junit.jupiter.api.Test;
import org.quartz.JobDataMap;
import ru.job4j.vacancy.TestUtil;

import java.time.LocalDateTime;
import java.util.Map;

import static java.time.LocalDateTime.of;
import static org.junit.jupiter.api.Assertions.*;
import static ru.job4j.vacancy.jsoup.ParserUtil.createJsoupProcessor;

class ParserUtilTest {

    @Test
    void specifySqlRuJsoupProcessor() {
        Map<String, Object> properties = Map.of(
                "vacancy.source", "sql.ru",
                "vacancy.keyword", "java"
        );
        checkClassOfJsoupProcessor(SqlRuJsoupProcessor.class, properties);
    }

    @Test
    void specifyHhRuJsoupProcessor() {
        Map<String, Object> properties = Map.of(
                "vacancy.source", "hh.ru",
                "vacancy.keyword", "java"
        );
        checkClassOfJsoupProcessor(HhRuJsoupProcessor.class, properties);
    }

    @Test
    void specifyHabrCareerJsoupProcessor() {
        Map<String, Object> properties = Map.of(
                "vacancy.source", "habr.com",
                "vacancy.keyword", "java"
        );
        checkClassOfJsoupProcessor(HabrCareerJsoupProcessor.class, properties);
    }

    @Test
    void specifySqlRuJsoupProcessorEmptyProperties() {
        Map<String, Object> properties = Map.of();
        checkClassOfJsoupProcessor(SqlRuJsoupProcessor.class, properties);
    }


    @Test
    void specifySqlRuJsoupProcessorInvalidProperties() {
        Map<String, Object> properties = Map.of("vacancy.source", "vk.ru");
        var thrown = assertThrows(IllegalArgumentException.class, () -> createJsoupProcessor(new JobDataMap(properties)));
        assertEquals("'vacancy.source' is missing or incorrect", thrown.getMessage());
    }

    @Test
    void javaFilterTrue() {
        matchTrue("java");
        matchTrue("java developer");
        matchTrue("[java] programmer");
        matchTrue("jAvA");
        matchTrue("Java.");
        matchTrue("Java/JS programmer");
        matchTrue("Java/Javascript programmer");
        matchTrue("Java программист");
        matchTrue("Javaprogrammer");
    }

    @Test
    void javaFilterFalse() {
        matchFalse("");
        matchFalse("developer");
        matchFalse("[jav] programmer");
        matchFalse("Javascript programmer");
        matchFalse("java script developer");
    }

    @Test
    void isNotBeforeUtil() {
        matchNotBefore(of(2015, 4, 3, 2, 1, 1));
        matchNotBefore(of(2015, 4, 3, 2, 1, 0));
        matchNotBefore(of(2015, 4, 3, 2, 1));

        matchBefore(of(2015, 4, 3, 2, 0, 59));
        matchBefore(of(2015, 4, 3, 2, 0));
    }

    private void checkClassOfJsoupProcessor(Class<? extends JsoupProcessor> expectedClass, Map<String, Object> properties) {
        var processor = createJsoupProcessor(new JobDataMap(properties));
        assertEquals(expectedClass, processor.getClass());
    }

    private void matchNotBefore(LocalDateTime dateTime) {
        var limit = of(2015, 4, 3, 2, 1);
        assertTrue(isNotBefore(dateTime, limit));
    }

    private void matchBefore(LocalDateTime dateTime) {
        var limit = of(2015, 4, 3, 2, 1);
        assertFalse(isNotBefore(dateTime, limit));
    }

    private static boolean isNotBefore(LocalDateTime checked, LocalDateTime limit) {
        return checked.compareTo(limit) >= 0;
    }

    private static void matchTrue(String line) {
        assertTrue(match(line));
    }

    private static void matchFalse(String line) {
        assertFalse(match(line));
    }

    private static boolean match(String line) {
        var javaFilter = TestUtil.JAVA_DEFAULT_PARAMS.getTitleFilter();
        return javaFilter.test(line);
    }
}
