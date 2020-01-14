package ru.job4j.vacancy.jsoup;

import org.junit.jupiter.api.Test;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import ru.job4j.vacancy.util.JsoupHelper.Filters;

import java.time.LocalDateTime;
import java.util.Map;

import static java.time.LocalDateTime.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.job4j.vacancy.util.JsoupHelper.specifyJsoupProcessor;
import static ru.job4j.vacancy.util.Util.isNotBefore;

public class JsoupHelperTest {
    @Test
    public void specifySqlRuJsoupProcessor() {
        Map<String, Object> properties = Map.of(
                "vacancy.site", "sql.ru",
                "vacancy.filter", "java"
        );
        checkClassOfJsoupProcessor(SqlRuJsoupProcessor.class, properties);
    }

    @Test
    public void specifyHhRuJsoupProcessor() {
        Map<String, Object> properties = Map.of(
                "vacancy.site", "hh.ru",
                "vacancy.filter", "java"
        );
        checkClassOfJsoupProcessor(HhRuJsoupProcessor.class, properties);
    }

    @Test
    public void specifyMoiKrugJsoupProcessor() {
        Map<String, Object> properties = Map.of(
                "vacancy.site", "moikrug.ru",
                "vacancy.filter", "java"
        );
        checkClassOfJsoupProcessor(HabrCareerJsoupProcessor.class, properties);
    }

    @Test
    public void specifySqlRuJsoupProcessorEmptyProperties() {
        Map<String, Object> properties = Map.of();
        checkClassOfJsoupProcessor(SqlRuJsoupProcessor.class, properties);
    }


    @Test
    public void specifySqlRuJsoupProcessorInvalidProperties() {
        Map<String, Object> properties = Map.of("vacancy.site", "vk.ru");
        checkClassOfJsoupProcessor(SqlRuJsoupProcessor.class, properties);
    }

    @Test
    public void javaFilterTrue() {
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
    public void javaFilterFalse() {
        matchFalse("");
        matchFalse("developer");
        matchFalse("[jav] programmer");
        matchFalse("Javascript programmer");
        matchFalse("java script developer");
    }

    @Test
    public void isNotBeforeUtil() {
        matchNotBefore(of(2015, 4, 3, 2, 1, 1));
        matchNotBefore(of(2015, 4, 3, 2, 1, 0));
        matchNotBefore(of(2015, 4, 3, 2, 1));

        matchBefore(of(2015, 4, 3, 2, 0, 59));
        matchBefore(of(2015, 4, 3, 2, 0));
    }

    private void checkClassOfJsoupProcessor(Class<? extends JsoupProcessor> expectedClass, Map<String, Object> properties) {
        JobExecutionContext mockCtx = mock(JobExecutionContext.class);
        JobDetail mockJob = mock(JobDetail.class);
        when(mockJob.getJobDataMap()).thenReturn(new JobDataMap(properties));
        when(mockCtx.getJobDetail()).thenReturn(mockJob);
        var processor = specifyJsoupProcessor(mockCtx);
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


    private void matchTrue(String line) {
        assertTrue(Filters.javaFilter(line));
    }

    private void matchFalse(String line) {
        assertFalse(Filters.javaFilter(line));
    }
}
