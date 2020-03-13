package ru.job4j.vacancy;

import org.assertj.core.util.DateUtil;
import org.junit.jupiter.api.Test;
import org.quartz.JobDataMap;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import ru.job4j.vacancy.jsoup.JsoupProcessor;
import ru.job4j.vacancy.jsoup.SqlRuJsoupProcessor;
import ru.job4j.vacancy.sql.SQLProcessor;
import ru.job4j.vacancy.util.TimeUtil;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.quartz.impl.StdSchedulerFactory.getDefaultScheduler;
import static ru.job4j.vacancy.TestUtil.mockCollectorJob;
import static ru.job4j.vacancy.job.VacancyCollectorJob.VACANCY_KEYWORD;
import static ru.job4j.vacancy.util.TimeUtil.now;
import static ru.job4j.vacancy.util.TimeUtilTest.assertMatch;

class JobExecutorTest {
    private static final String JOB_KEY = "testJob";

    @Test
    void schedule() throws Exception {
        SQLProcessor mockSqlProcessor = mock(SQLProcessor.class);
        JsoupProcessor mockJsoupProcessor = mock(SqlRuJsoupProcessor.class);
        var inputProperties = Map.of(
                SQLProcessor.class.getSimpleName(), mockSqlProcessor,
                JsoupProcessor.class.getSimpleName(), mockJsoupProcessor,
                VACANCY_KEYWORD, "java"
        );
        var cronExpression = "0 0 12 * * ?"; // every day 12:00

        JobExecutor executor = new JobExecutor();
        executor.start();
        Trigger jobTrigger = executor.schedule(mockCollectorJob(), JOB_KEY, null, cronExpression, inputProperties);
        JobDataMap dataMap = getDefaultScheduler().getJobDetail(jobTrigger.getJobKey()).getJobDataMap();
        executor.shutdown(false);

        ZonedDateTime now = now();
        Date actual = jobTrigger.getNextFireTime();
        ZonedDateTime expected = nextMidday(now);
        assertMatch(actual, expected);

        // only default keys required
        assertEquals(mockSqlProcessor, dataMap.get(SQLProcessor.class.getSimpleName()));
        assertEquals(mockJsoupProcessor, dataMap.get(JsoupProcessor.class.getSimpleName()));
    }

    private ZonedDateTime nextMidday(ZonedDateTime now) {
        var result = now;
        if (!now.isBefore(ZonedDateTime.now().withHour(12).withMinute(0))) {
            result = result.plusDays(1);
        }
        return result.withHour(12).withMinute(0);
    }

    @Test
    void scheduleWithExtraProperties() throws Exception {
        var inputProperties = Map.of(
                "db.driver", "testDriver",
                "db.url", "testUrl",
                "db.username", "testUsername",
                "db.password", "testPassword");

        JobExecutor executor = new JobExecutor();
        executor.start();

        var requiredKeys = List.of("db.username", "db.password");
        Trigger jobTrigger = executor.schedule(mockCollectorJob(requiredKeys), JOB_KEY, DateUtil.tomorrow(), null, inputProperties);
        JobDataMap dataMap = executor.getScheduler().getJobDetail(jobTrigger.getJobKey()).getJobDataMap();
        executor.shutdown(false);

        // only username and password required
        assertEquals("testUsername", dataMap.getString("db.username"));
        assertEquals("testPassword", dataMap.getString("db.password"));
        assertNull(dataMap.getString("db.driver"));
        assertNull(dataMap.getString("db.url"));
    }

    @Test
    void scheduleWithoutCronExpression() throws SchedulerException {
        var properties = Map.of(
                "db.driver", "testDriver",
                "db.url", "testUrl",
                "db.username", "testUsername",
                "db.password", "testPassword");
        List<String> requiredKeys = List.of("db.driver", "db.url");

        JobExecutor executor = new JobExecutor();
        executor.start();

        Trigger jobTrigger = executor.schedule(mockCollectorJob(requiredKeys), JOB_KEY, null, null, properties);
        executor.shutdown(false);

        Date actual = jobTrigger.getNextFireTime();
        ZonedDateTime expected = now();
        assertMatch(actual, expected);
    }

    @Test
    void scheduleWithNextLaunch() throws SchedulerException {
        var properties = Map.of(
                "db.driver", "testDriver",
                "db.url", "testUrl");
        List<String> requiredKeys = List.of("db.driver", "db.url");

        JobExecutor executor = new JobExecutor();
        executor.start();

        ZonedDateTime nextLaunch = TimeUtil.now().plusDays(1);

        Trigger jobTrigger = executor.schedule(mockCollectorJob(requiredKeys), JOB_KEY, Date.from(nextLaunch.toInstant()), null, properties);
        executor.shutdown(false);

        assertMatch(jobTrigger.getNextFireTime(), nextLaunch);
    }

    @Test
    void scheduleWithoutRequiredProperty() throws SchedulerException {
        var properties = Map.of(
                "db.driver", "testDriver",
                "db.url", "testUrl",
                "db.password", "testPassword");
        var requiredKeys = List.of("db.username", "db.password");

        JobExecutor executor = new JobExecutor();
        executor.start();
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class,
                () -> executor.schedule(mockCollectorJob(requiredKeys), JOB_KEY, null, null, properties));
        executor.shutdown(false);

        assertEquals("'db.username' is missing or incorrect", thrown.getMessage());
    }


}
