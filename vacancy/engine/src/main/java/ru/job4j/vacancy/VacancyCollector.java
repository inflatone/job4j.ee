package ru.job4j.vacancy;

import one.util.streamex.StreamEx;
import org.quartz.Job;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import ru.job4j.vacancy.job.VacancyCollectorJob;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

/**
 * Starter class to execute Job implemented tasks
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-07-27
 */
public class VacancyCollector {
    private static final Map<Class<? extends Job>, List<String>> REQUIRED_KEYS
            = Map.of(VacancyCollectorJob.class, List.of("db.driver", "db.url", "db.username", "db.password"));

    private static final Map<Class<? extends Job>, List<String>> ADDITIONAL_KEYS
            = Map.of(VacancyCollectorJob.class, List.of("vacancy.site", "vacancy.filter"));

    private static final String DEBUG = "-debug";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("d.MM.yyyy H:mm:ss");

    private final List<String> properties;
    private final boolean isDebug;

    private JobExecutor jobExecutor;

    public VacancyCollector(String[] properties) {
        this.properties = asList(properties);
        isDebug = this.properties.contains(DEBUG);
    }

    /**
     * Shut the executor down
     *
     * @param waitForJobsToComplete if started jobs need to wait
     * @return if executor was stopped
     * @throws SchedulerException if an error occurs due to terminate the scheduler
     */
    public boolean shutdown(boolean waitForJobsToComplete) throws SchedulerException {
        var result = false;
        if (jobExecutor != null) {
            jobExecutor.shutdown(waitForJobsToComplete);
            result = true;
        }
        return result;
    }

    /**
     * Logs the exception occurred during program execution, considering if debug mode is turned on
     *
     * @param e occurred exception
     */
    public void handleException(Logger log, Exception e) {
        if (!isDebug) {
            log.error("Error: " + e.getMessage());
        } else {
            log.error("Error: " + e.getMessage(), e);
        }
    }

    /**
     * Checks given program arguments and starts main program logic class if okay
     */
    public String start(Class<? extends Job> jobClass) throws SchedulerException, IOException {
        startExecutor();
        Date startDate = jobExecutor.execute(jobClass, REQUIRED_KEYS.getOrDefault(jobClass, List.of()),
                ADDITIONAL_KEYS.getOrDefault(jobClass, List.of()));
        String formattedDateTime = LocalDateTime.ofInstant(
                startDate.toInstant(), ZoneId.systemDefault()
        ).format(FORMATTER);
        return "The next vacancy scan has been scheduled on " + formattedDateTime;
    }

    private void startExecutor() throws SchedulerException, IOException {
        if (jobExecutor == null) {
            jobExecutor = new JobExecutor();
            jobExecutor.start();
            for (String propertyPath : getAppProperties()) {
                try (InputStream in = new FileInputStream(propertyPath)) {
                    jobExecutor.loadProperties(in);
                }
            }
        }
    }

    private List<String> getAppProperties() {
        List<String> propertiesFiles = StreamEx.of(this.properties).filter(p -> p.endsWith(".properties")).toList();
        if (propertiesFiles.isEmpty()) {
            throw new IllegalArgumentException("need single *.properties file path at least");
        }
        return propertiesFiles;
    }
}