package ru.job4j.vacancy;

import lombok.Getter;
import one.util.streamex.StreamEx;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import ru.job4j.vacancy.job.ExecutorJob;
import ru.job4j.vacancy.jsoup.ParserUtil;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import static org.quartz.CronScheduleBuilder.cronSchedule;

/**
 * Represents universal scheduler executor of any class implementing {@link Job}
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-07-30
 */
@Getter
public class JobExecutor {
    private final Scheduler scheduler;

    public JobExecutor() throws SchedulerException {
        this(null);
    }

    public JobExecutor(String propertyPath) throws SchedulerException {
        var factory = propertyPath == null ? new StdSchedulerFactory() : new StdSchedulerFactory(propertyPath);
        this.scheduler = factory.getScheduler();
    }

    /**
     * Turns on job executor scheduler
     */
    public void start() throws SchedulerException {
        scheduler.start();
    }

    /**
     * Shut the scheduler down
     *
     * @param waitForJobsToComplete if started jobs need to wait
     */
    public void shutdown(boolean waitForJobsToComplete) throws SchedulerException {
        scheduler.shutdown(waitForJobsToComplete);
    }

    /**
     * Sets to execute the job based on the given job class
     *
     * @param job            job
     * @param jobKey         job key
     * @param launch         next launch date
     * @param cronExpression cron expression
     * @param properties     property map of required attributes
     * @return job trigger
     */
    public Trigger schedule(ExecutorJob job, String jobKey, Date launch, String cronExpression, Map<String, ?> properties) throws SchedulerException {
        return schedule(job, jobKey, buildTrigger(jobKey, launch, cronExpression), properties);
    }

    protected Trigger schedule(ExecutorJob job, String key, Trigger trigger, Map<String, ?> properties) throws SchedulerException {
        var detail = buildJobDetail(key, job, properties);
        scheduler.scheduleJob(detail, trigger);
        return trigger;
    }

    /**
     * Composes a Trigger object based on cronExpression  and next launch property values
     *
     * @param jobKey         job id
     * @param launch         next launch date
     * @param cronExpression cron expression as job repeat rule
     * @return composed {@link Trigger} object
     */
    protected static Trigger buildTrigger(String jobKey, @Nullable Date launch, @Nullable String cronExpression) {
        var builder = TriggerBuilder.newTrigger().withIdentity(jobKey);
        if (launch != null) {
            builder.startAt(launch);
        }
        if (cronExpression != null) {
            builder.withSchedule(cronSchedule(cronExpression));
        }
        return builder.build();
    }

    /**
     * Composes a JodDetail object applying the configurator based on the given (during instantiation) property keys
     *
     * @param jobKey     job id
     * @param job        job to execute
     * @param properties job properties
     * @return composed {@link JobDetail} object
     */
    protected static JobDetail buildJobDetail(String jobKey, ExecutorJob job, Map<String, ?> properties) {
        var jobBuilder = JobBuilder.newJob(job.getClass());
        return createBuilderConfigurator(job, properties)
                .apply(jobBuilder)
                .withIdentity(jobKey)
                .build();
    }

    /**
     * Creates {@link JobBuilder} configurator, which appending all the properties
     * associated with the given keys to the future job's data map
     *
     * @param job        job to execute
     * @param properties job properties
     * @return configurator to be applied to {@link JobBuilder}'s object
     */
    private static UnaryOperator<JobBuilder> createBuilderConfigurator(ExecutorJob job, Map<String, ?> properties) {
        return builder -> {
            JobDataMap requiredMap = StreamEx.of(job.getRequiredKeys()).collect(
                    Collectors.toMap(key -> (String) key, key -> ParserUtil.necessarilyGet(() -> properties.get(key), key),
                            (v1, v2) -> v2, JobDataMap::new));

            var additionalKeys = job.getAdditionalKeys();
            JobDataMap additionalMap = StreamEx.of(properties.entrySet())
                    .filter(e -> additionalKeys.contains(e.getKey()))
                    .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (v1, v2) -> v1, JobDataMap::new));
            builder.usingJobData(requiredMap);
            builder.usingJobData(additionalMap);
            return builder;
        };
    }
}