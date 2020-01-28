package ru.job4j.jobseeker.service.executor;

import one.util.streamex.StreamEx;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerKey;
import org.quartz.spi.JobFactory;
import ru.job4j.vacancy.JobExecutor;
import ru.job4j.vacancy.util.ExceptionUtil;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

import static org.quartz.JobKey.jobKey;
import static org.quartz.TriggerKey.triggerKey;
import static ru.job4j.vacancy.util.ExceptionUtil.handleGet;
import static ru.job4j.vacancy.util.ExceptionUtil.nullSafely;

/**
 * Represents web scheduler executor to launch task jobs
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2020-01-28
 */
public class WebJobExecutor extends JobExecutor {
    private static final TaskLauncherJob JOB = new TaskLauncherJob(null, null);

    private final JobFactory jobFactory;

    public WebJobExecutor(String propertyPath, JobFactory jobFactory) throws SchedulerException {
        super(propertyPath);
        this.jobFactory = jobFactory;
    }

    @Override
    public void start() throws SchedulerException {
        getScheduler().setJobFactory(jobFactory);
        super.start();
    }

    /**
     * Sets to execute the job based on the given job class
     *
     * @param key            job key
     * @param launch         next launch date
     * @param cronExpression cron expression
     * @param properties     property map of required attributes
     * @return job trigger
     */
    public Trigger schedule(String key, boolean active, Date launch, String cronExpression, Map<String, ?> properties) {
        var trigger = buildTrigger(key, launch, cronExpression);
        return ExceptionUtil.handleGet(() -> {
            var result = super.schedule(JOB, key, trigger, properties);
            pauseNew(trigger.getKey(), !active);
            return result;
        });
    }

    /**
     * Reschedules the job associated with the given key
     *
     * @param key            job id
     * @param active         is active task
     * @param launch         next launch date
     * @param cronExpression cron expression
     * @param properties     property map of required attributes
     * @return job trigger
     */
    public Trigger reschedule(String key, boolean active, Date launch, String cronExpression, Map<String, ?> properties) {
        return handleGet(() -> {
            var oldTrigger = retrieveTrigger(key);
            var newTrigger = buildTrigger(key, launch(launch, oldTrigger), cronExpression);
            if (oldTrigger == null) {
                super.schedule(JOB, key, newTrigger, properties);
            } else {
                getScheduler().rescheduleJob(oldTrigger.getKey(), newTrigger);
            }
            pauseNew(newTrigger.getKey(), !active);
            return newTrigger;
        });
    }

    private Trigger retrieveTrigger(String code) throws SchedulerException {
        return findTrigger(code).orElse(null);
    }

    private Optional<? extends Trigger> findTrigger(String code) throws SchedulerException {
        return StreamEx.of(getScheduler().getTriggersOfJob(jobKey(code)))
                .findFirst(t -> code.equals(t.getKey().getName()));
    }

    private static Date launch(Date launch, Trigger trigger) {
        return launch != null ? launch
                : (trigger == null ? null : trigger.getNextFireTime());
    }

    /**
     * Pauses/resumes the job associated with the given key
     *
     * @param code   job id
     * @param paused need to be paused or resumed
     */
    public void pause(String code, boolean paused) {
        ExceptionUtil.handleRun(() -> nullSafely(retrieveTrigger(code), t -> {
            pause(t.getKey(), paused);
            return null;
        }));
    }

    private void pauseNew(TriggerKey triggerKey, boolean paused) throws SchedulerException {
        if (paused) {
            pause(triggerKey, true);
        }
    }

    private void pause(TriggerKey triggerKey, boolean paused) throws SchedulerException {
        if (paused) {
            getScheduler().pauseTrigger(triggerKey);
        } else {
            getScheduler().resumeTrigger(triggerKey);
        }
    }

    /**
     * Unschedules the job associated with the given key
     *
     * @param key job id
     * @return true is successful
     */
    public boolean unschedule(String key) {
        return handleGet(() -> getScheduler().unscheduleJob(triggerKey(key)));
    }

    /**
     * Retrieves the job next execution time from the trigger associated with the given key
     *
     * @param key job id
     * @return next execution time, or null if job's not scheduled
     */
    public Date getNextLaunch(String key) {
        return handleGet(() -> findTrigger(key).map(Trigger::getNextFireTime).orElse(null));
    }
}