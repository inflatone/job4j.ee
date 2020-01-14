package ru.job4j.vacancy;

import com.google.common.collect.ImmutableMap;
import one.util.streamex.StreamEx;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.slf4j.Logger;
import ru.job4j.vacancy.job.ExecutorJob;
import ru.job4j.vacancy.job.VacancyCollectorJob;
import ru.job4j.vacancy.jsoup.JsoupProcessor;
import ru.job4j.vacancy.jsoup.ParserUtil;
import ru.job4j.vacancy.sql.SQLProcessor;
import ru.job4j.vacancy.util.TimeUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

import static ru.job4j.vacancy.job.VacancyCollectorJob.VACANCY_KEYWORD;

/**
 * Starter class to execute Job implemented tasks
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-07-27
 */
public class VacancyCollector {
    private static final String DEFAULT_SEARCH_KEY = "java";

    public static final String CRON_EXPRESSION_KEY = "cron.expression";
    public static final String NEXT_LAUNCH_KEY = "next.launch";

    private static final String DEBUG = "-debug";

    private final Set<String> params;
    private final boolean isDebug;

    private JobExecutor jobExecutor;

    public VacancyCollector(String... params) {
        this.params = Set.of(params);
        isDebug = this.params.contains(DEBUG);
    }

    /**
     * Shut the executor down
     *
     * @param waitForJobsToComplete if started jobs need to wait
     * @return if executor was stopped
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
     * Starts vacancy parsing executor with default job object
     *
     * @return vacancy collector job trigger
     */
    public Trigger start() throws IOException, SchedulerException {
        return start(new VacancyCollectorJob());
    }

    /**
     * Checks given program arguments and starts main program logic class if okay
     *
     * @param job job
     * @return new job's trigger
     */
    public Trigger start(ExecutorJob job) throws IOException, SchedulerException {
        startExecutor();
        var jobProperties = createAppProperties();
        var nextStart = (String) jobProperties.get(NEXT_LAUNCH_KEY);
        var cronExpression = (String) jobProperties.get(CRON_EXPRESSION_KEY);
        return jobExecutor.schedule(job, VacancyCollectorJob.class.getSimpleName(),
                nextStart == null ? null : TimeUtil.toDate(nextStart),
                cronExpression, jobProperties);
    }

    /**
     * Composes necessary app properties based on the given start keys
     *
     * @return app properties
     */
    private Map<String, Object> createAppProperties() throws IOException {
        Properties properties = collectAppProperties();
        var jobPropertiesBuilder = ImmutableMap.<String, Object>builder()
                .put(JsoupProcessor.class.getSimpleName(), ParserUtil.createJsoupProcessor(properties))
                .put(SQLProcessor.class.getSimpleName(), ParserUtil.createSQLProcessor(properties))
                .put(VACANCY_KEYWORD, properties.getOrDefault(VACANCY_KEYWORD, DEFAULT_SEARCH_KEY));

        properties.remove(VACANCY_KEYWORD);
        for (Enumeration<?> e = properties.propertyNames(); e.hasMoreElements(); ) {
            String key = (String) e.nextElement();
            jobPropertiesBuilder.put(key, properties.getProperty(key));
        }
        return jobPropertiesBuilder.build();
    }

    private void startExecutor() throws SchedulerException {
        if (jobExecutor == null) {
            jobExecutor = new JobExecutor();
            jobExecutor.start();
        }
    }

    /**
     * Collects properties from the given file paths that were received during object initialization
     *
     * @return properties
     */
    private Properties collectAppProperties() throws IOException {
        var buffer = new Properties();
        for (String propertyPath : getAppProperties()) {
            try (var in = new FileInputStream(propertyPath)) {
                buffer.load(in);
            }
        }
        return buffer;
    }

    /**
     * Retrieves the *.properties paths from the app args
     *
     * @return property file paths
     */
    private List<String> getAppProperties() {
        List<String> propertiesFiles = StreamEx.of(this.params).filter(p -> p.endsWith(".properties")).toList();
        if (propertiesFiles.isEmpty()) {
            throw new IllegalArgumentException("need single *.properties file path at least");
        }
        return propertiesFiles;
    }
}