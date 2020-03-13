package ru.job4j.vacancy;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.quartz.ObjectAlreadyExistsException;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import ru.job4j.vacancy.jsoup.JsoupProcessor;
import ru.job4j.vacancy.sql.SQLProcessor;

import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.quartz.impl.StdSchedulerFactory.getDefaultScheduler;
import static ru.job4j.vacancy.TestUtil.*;
import static ru.job4j.vacancy.job.VacancyCollectorJob.VACANCY_CITY;
import static ru.job4j.vacancy.job.VacancyCollectorJob.VACANCY_KEYWORD;

class VacancyCollectorTest {
    @Test
    void start() throws Exception {
        String jobProperties = asFullPathString("job.properties");
        String[] args = {jobProperties};
        var collector = new VacancyCollector(args);
        var mockJob = mockCollectorJob();
        Trigger jobTrigger = collector.start(mockJob);

        Map<String, Object> dataMap = getDefaultScheduler().getJobDetail(jobTrigger.getJobKey()).getJobDataMap();
        assertTrue(collector.shutdown(false));

        assertEquals("java", dataMap.get(VACANCY_KEYWORD));
        assertNull(dataMap.get(VACANCY_CITY));
    }

    @Test
    void startWithAdditionalProperties(@TempDir Path tempDir) throws Exception {
        String defaultPropertiesPath = asFullPathString("job.properties");

        var properties = new Properties();
        properties.setProperty(VACANCY_KEYWORD, "python");
        properties.setProperty(VACANCY_CITY, "Moscow");

        String additionalPropertiesPath = writeProperties(tempDir.resolve("additional.properties"), properties);

        String[] args = {defaultPropertiesPath, additionalPropertiesPath};
        var collector = new VacancyCollector(args);
        Trigger jobTrigger = collector.start(mockCollectorJob());

        Map<String, Object> dataMap = getDefaultScheduler().getJobDetail(jobTrigger.getJobKey()).getJobDataMap();
        assertTrue(collector.shutdown(false));

        assertEquals("python", dataMap.get(VACANCY_KEYWORD));
        assertEquals("Moscow", dataMap.get(VACANCY_CITY));

        assertNotNull(dataMap.get(SQLProcessor.class.getSimpleName()));
        assertNotNull(dataMap.get(JsoupProcessor.class.getSimpleName()));
    }


    @Test
    void startWithoutProperties() throws SchedulerException {
        var collector = new VacancyCollector();
        var thrown = assertThrows(IllegalArgumentException.class, () -> collector.start(mockCollectorJob()));
        assertEquals("need single *.properties file path at least", thrown.getMessage());
        collector.shutdown(false);
    }

    @Test
    void shutdownBeforeStart() throws SchedulerException {
        var collector = new VacancyCollector();
        assertFalse(collector.shutdown(false));
    }

    @Test
    void startDuplicateTask() throws Exception {
        String jobProperties = asFullPathString("job.properties");
        String[] args = {jobProperties};
        var collector = new VacancyCollector(args);
        var mockJob = mockCollectorJob();
        collector.start(mockJob);
        var thrown = assertThrows(ObjectAlreadyExistsException.class, () -> collector.start(mockJob));
        assertTrue(thrown.getMessage().contains("already exists"));
        collector.shutdown(false);
    }

    @Test
    void debugTurnedOn() {
        String[] args = {"-debug"};
        var collector = new VacancyCollector(args);
        Logger mockLogger = mock(Logger.class);

        doAnswer(i -> {
            String message = i.getArgument(0);
            assertEquals("Error: test exception", message);
            return null;
        }).when(mockLogger).error(anyString(), any(Throwable.class));

        doAnswer(i -> {
            fail("not expected");
            return null;
        }).when(mockLogger).error(anyString());

        collector.handleException(mockLogger, new IllegalArgumentException("test exception"));
    }

    @Test
    void debugTurnedOff() {
        String[] args = {};
        var collector = new VacancyCollector(args);
        Logger mockLogger = mock(Logger.class);

        doAnswer(i -> {
            fail("not expected");
            return null;
        }).when(mockLogger).error(anyString(), any(Throwable.class));

        doAnswer(i -> {
            String message = i.getArgument(0);
            assertEquals("Error: test exception", message);
            return null;

        }).when(mockLogger).error(anyString());

        collector.handleException(mockLogger, new IllegalArgumentException("test exception"));
    }
}