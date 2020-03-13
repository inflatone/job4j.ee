package ru.job4j.vacancy;

import org.quartz.JobExecutionContext;
import ru.job4j.vacancy.job.ExecutorJob;
import ru.job4j.vacancy.job.VacancyCollectorJob;
import ru.job4j.vacancy.jsoup.ParseParameters;
import ru.job4j.vacancy.model.VacancyData;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Properties;

import static com.google.common.io.Resources.getResource;
import static java.time.Month.JULY;
import static java.time.Month.JUNE;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.job4j.vacancy.util.TimeUtil.now;

public class TestUtil {
    public static final VacancyData VACANCY1 = new VacancyData("first", "link_1", "description_1", of(2019, JUNE, 30, 15, 17));
    public static final VacancyData VACANCY2 = new VacancyData("second", "link_2", "description_2", of(2019, JUNE, 28, 10, 13));
    public static final VacancyData VACANCY3 = new VacancyData("third", "link_3", "description_3", of(2019, JUNE, 26, 21, 48));
    public static final VacancyData NEW_VACANCY = new VacancyData("new", "new_link", "new_description", of(2019, JULY, 2, 18, 0));

    public static final List<VacancyData> VACANCIES = List.of(VACANCY1, VACANCY2, VACANCY3);

    public static final List<VacancyData> UPDATED_VACANCIES = List.of(NEW_VACANCY, VACANCY1, VACANCY2, VACANCY3);

    public static final ParseParameters JAVA_DEFAULT_PARAMS = ParseParameters.of("java",
            LocalDateTime.now().with(LocalTime.MIN).minusYears(10).atZone(ZoneId.systemDefault()));

    public static ZonedDateTime of(int year, Month month, int dayOfMonth, int hour, int minute) {
        return ZonedDateTime.of(LocalDateTime.of(year, month, dayOfMonth, hour, minute), ZoneId.systemDefault());
    }

    public static ZonedDateTime of(int year, Month month, int dayOfMonth) {
        return ZonedDateTime.of(LocalDate.of(year, month, dayOfMonth), LocalTime.now(), ZoneId.systemDefault()).truncatedTo(ChronoUnit.MINUTES);
    }

    public static VacancyData getDuplicate(VacancyData vacancy) {
        return new VacancyData(vacancy.getTitle(), "new_" + vacancy.getUrl(), "new_" + vacancy.getDescription(), now());
    }

    public static String asFullPathString(String resourceName) throws URISyntaxException {
        return Paths.get(getResource(resourceName).toURI()).toString();
    }

    public static String writeProperties(Path output, Properties properties) throws IOException {
        Path file = Files.createFile(output);
        System.out.println("Created: " + file);
        try (var writer = Files.newBufferedWriter(output)) {
            properties.store(writer, null);
        }
        return output.toString();
    }

    // job class with no execution
    public static ExecutorJob mockCollectorJob(List<String> requiredKeys, String... additionalKeys) {
        var mockJob = mock(MockVacancyCollectorJob.class);
        when(mockJob.getRequiredKeys()).thenReturn(requiredKeys);
        when(mockJob.getAdditionalKeys()).thenReturn(List.of(additionalKeys));
        return mockJob;
    }

    public static ExecutorJob mockCollectorJob() {
        return new MockVacancyCollectorJob();
    }

    public static class MockVacancyCollectorJob extends VacancyCollectorJob {
        @Override
        public void execute(JobExecutionContext context) {
            // do nothing
        }
    }
}