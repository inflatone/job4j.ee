package ru.job4j.vacancy;

import org.quartz.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.function.UnaryOperator;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static org.quartz.CronScheduleBuilder.cronSchedule;
import static org.quartz.TriggerBuilder.newTrigger;
import static org.quartz.impl.StdSchedulerFactory.getDefaultScheduler;

/**
 * Represents universal scheduler executor of any class implementing {@link Job}
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-07-30
 */
public class JobExecutor {
    private final Properties properties = new Properties();
    private final Scheduler scheduler;

    public JobExecutor() throws SchedulerException {
        this.scheduler = getDefaultScheduler();
    }

    /**
     * Turns on job executor scheduler
     *
     * @throws SchedulerException if an error occurs due to start the scheduler
     */
    public void start() throws SchedulerException {
        scheduler.start();
    }

    /**
     * Shut the scheduler down
     *
     * @param waitForJobsToComplete if started jobs need to wait
     * @throws SchedulerException if an error occurs due to terminate the scheduler
     */
    public void shutdown(boolean waitForJobsToComplete) throws SchedulerException {
        scheduler.shutdown(waitForJobsToComplete);
    }

    /**
     * Returns the scheduler object for the advanced use
     *
     * @return scheduler
     */
    public Scheduler scheduler() {
        return scheduler;
    }

    /**
     * Add app properties from the given input stream
     *
     * @param in input stream
     * @throws IOException if an IO error occurs
     */
    public void loadProperties(InputStream in) throws IOException {
        properties.load(in);
    }

    /**
     * Sets to execute the job based on the given job class
     *
     * @param jobClass           job class
     * @param requiredKeysForJob list of property keys required for the job execution
     * @return date of job's first start
     * @throws SchedulerException if an error occurs due to schedule the job
     */
    public Date execute(Class<? extends Job> jobClass, List<String> requiredKeysForJob, List<String> additionalKeysForJob) throws SchedulerException {
        JobConfigurator jobConfigurator = new JobConfigurator(jobClass, requiredKeysForJob, additionalKeysForJob);
        Trigger trigger = jobConfigurator.trigger();
        JobDetail job = jobConfigurator.job();
        return scheduler.scheduleJob(job, trigger);
    }

    /**
     * Encapsulates the logic of composing JobDetail and Trigger objects, associated with an outer class object
     */
    class JobConfigurator {
        private final List<String> requiredKeysForJob;
        private final List<String> additionalKeysForJob;
        private final Class<? extends Job> jobClass;

        JobConfigurator(Class<? extends Job> jobClass, List<String> requiredKeysForJob, List<String> additionalKeysForJob) {
            this.requiredKeysForJob = requiredKeysForJob;
            this.additionalKeysForJob = additionalKeysForJob;
            this.jobClass = jobClass;
        }

        /**
         * Composes a Trigger object based on properties' cronExpression value
         *
         * @return composed {@link Trigger} object
         */
        Trigger trigger() {
            var trigger = newTrigger();
            var cronExpression = properties.getProperty("cron.time");
            return cronExpression == null ? trigger.build()
                    : trigger.withSchedule(cronSchedule(cronExpression)).build();
        }

        /**
         * Composes a JodDetail object applying the configurator based on the given (during instantiation) property keys
         *
         * @return composed {@link JobDetail} object
         */
        JobDetail job() {
            var jobBuilder = JobBuilder.newJob(jobClass);
            return createBuilderConfigurator()
                    .apply(jobBuilder)
                    .withIdentity(jobClass.getSimpleName())
                    .build();
        }

        /**
         * Creates {@link JobBuilder} configurator, which appending all the properties
         * associated with the given keys to the future job's data map
         *
         * @return configurator to be applied to {@link JobBuilder}'s object
         */
        private UnaryOperator<JobBuilder> createBuilderConfigurator() {
            return builder -> {
                requiredKeysForJob.forEach(key -> builder.usingJobData(key, getIfNonNull(key)));
                additionalKeysForJob.forEach(key -> builder.usingJobData(key, properties.getProperty(key)));
                return builder;
            };
        }

        /**
         * Retrieves the property value associated with the given key, checking its availability
         *
         * @param key property key
         * @return property value
         * @throws IllegalStateException if property value's missed
         */
        private String getIfNonNull(String key) {
            return requireNonNull(properties.getProperty(key), format("'%s' must be set in property file", key));
        }
    }
}