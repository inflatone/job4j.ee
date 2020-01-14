package ru.job4j.vacancy.job;

import one.util.streamex.StreamEx;
import org.junit.jupiter.api.Test;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobKey;
import org.quartz.impl.JobDetailImpl;
import ru.job4j.vacancy.jsoup.JsoupProcessor;
import ru.job4j.vacancy.model.VacancyData;
import ru.job4j.vacancy.sql.SQLProcessor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.job4j.vacancy.TestUtil.VACANCIES;
import static ru.job4j.vacancy.TestUtil.VACANCY1;

class VacancyCollectorJobTest {
    @Test
    void execute() {
        SQLProcessor sqlProcessor = mock(SQLProcessor.class);

        List<VacancyData> buffer = new ArrayList<>();
        when(sqlProcessor.saveAll(anyList(), any())).then(invocation -> {
            List<VacancyData> vacancies = invocation.getArgument(0);
            buffer.addAll(vacancies);
            return buffer.size();
        });

        when(sqlProcessor.retrieveLastDate()).thenReturn(Timestamp.from(VACANCY1.getDateTime().toInstant()));
        JsoupProcessor jsoupProcessor = params -> StreamEx.of(VACANCIES)
                .filter(v -> !params.isReachLimit(v.getDateTime()))
                .toList();

        var mockCtx = createMockJobContext(sqlProcessor, jsoupProcessor);
        VacancyCollectorJob job = new VacancyCollectorJob();

        job.execute(mockCtx);

        assertEquals(1, buffer.size());
        assertEquals(List.of(VACANCY1), buffer);
    }


    @Test
    void executeFirstTime() {
        JsoupProcessor jsoupProcessor = params -> List.copyOf(VACANCIES);
        SQLProcessor sqlProcessor = mock(SQLProcessor.class);

        List<VacancyData> buffer = new ArrayList<>();
        when(sqlProcessor.saveAll(anyList(), any())).then(invocation -> {
            List<VacancyData> vacancies = invocation.getArgument(0);
            buffer.addAll(vacancies);
            return buffer.size();
        });

        var mockCtx = createMockJobContext(sqlProcessor, jsoupProcessor);
        VacancyCollectorJob job = new VacancyCollectorJob();

        job.execute(mockCtx);

        assertEquals(VACANCIES.size(), buffer.size());
        assertEquals(VACANCIES, buffer);
    }


    @Test
    void executeWithConnectionError() {
        JsoupProcessor jsoupProcessor = params -> List.of();
        var sqlProcessor = new SQLProcessor(() -> {
            throw new IllegalStateException("test connection error");
        });
        var mockCtx = createMockJobContext(sqlProcessor, jsoupProcessor);
        VacancyCollectorJob job = new VacancyCollectorJob();

        var thrown = assertThrows(IllegalStateException.class, () -> job.execute(mockCtx));
        assertEquals("test connection error", thrown.getMessage());
    }

    private static JobExecutionContext createMockJobContext(SQLProcessor sqlProcessor, JsoupProcessor jsoupProcessor) {
        return createMockJobContext(
                Map.of(SQLProcessor.class.getSimpleName(), sqlProcessor,
                        JsoupProcessor.class.getSimpleName(), jsoupProcessor,
                        VacancyCollectorJob.VACANCY_KEYWORD, "java"
                ));
    }

    private static JobExecutionContext createMockJobContext(final Map<String, Object> properties) {
        JobExecutionContext mockCtx = mock(JobExecutionContext.class);
        var mockJobDetail = new JobDetailImpl();
        mockJobDetail.setKey(JobKey.jobKey("Mock task"));
        mockJobDetail.setJobDataMap(new JobDataMap(properties));
        when(mockCtx.getJobDetail()).thenReturn(mockJobDetail);

        when(mockCtx.getNextFireTime()).thenReturn(new Date()); // just to prevent NPE
        return mockCtx;
    }
}