package ru.job4j.vacancy;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import ru.job4j.vacancy.TestUtil.ConnectionHolder;
import ru.job4j.vacancy.job.VacancyCollectorJob;
import ru.job4j.vacancy.sql.ConnectionProxy;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import static com.google.common.io.Resources.getResource;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VacancyCollectorJobTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void testExecuteWithInvalidDriver() {
        Map<String, Object> properties = Map.of(
                "db.driver", "incorrect_jdbc_driver",
                "db.url", "test",
                "db.username", "test",
                "db.password", "test",
                "proxy", (ConnectionProxy) ConnectionHolder::rollbackProxy
        );

        VacancyCollectorJob job = new VacancyCollectorJob();
        JobExecutionContext mockCtx = mock(JobExecutionContext.class);
        JobDetail mockJob = mock(JobDetail.class);
        when(mockJob.getJobDataMap()).thenReturn(new JobDataMap(properties));

        when(mockCtx.getJobDetail()).thenReturn(mockJob);
        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("JDBC driver not found");

        job.execute(mockCtx);
    }

    @Test
    public void testExecuteWithConnectionError() throws IOException {
        Properties properties = new Properties();
        try (InputStream in = getResource("job.properties").openStream()) {
            properties.load(in); // need real credentials to wrap working connection object
        }
        properties.put("proxy", (ConnectionProxy) conn -> {
            throw new IllegalStateException("test connection error");
        });

        VacancyCollectorJob job = new VacancyCollectorJob();
        JobExecutionContext mockCtx = mock(JobExecutionContext.class);
        JobDetail mockJob = mock(JobDetail.class);
        when(mockJob.getJobDataMap()).thenReturn(new JobDataMap(properties));
        when(mockCtx.getJobDetail()).thenReturn(mockJob);

        thrown.expect(IllegalStateException.class);
        thrown.expectMessage("test connection error");
        job.execute(mockCtx);
    }
}
