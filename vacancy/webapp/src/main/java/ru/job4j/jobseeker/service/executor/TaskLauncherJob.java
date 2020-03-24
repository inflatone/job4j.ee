package ru.job4j.jobseeker.service.executor;

import org.quartz.JobExecutionContext;
import ru.job4j.jobseeker.exeption.ApplicationException;
import ru.job4j.jobseeker.model.BaseEntity;
import ru.job4j.jobseeker.model.Task;
import ru.job4j.vacancy.job.ExecutorJob;
import ru.job4j.vacancy.util.ExceptionUtil;

import javax.inject.Inject;
import java.util.List;

import static ru.job4j.jobseeker.service.executor.ExecutionHelper.code;
import static ru.job4j.vacancy.util.ExceptionUtil.nullSafely;

/**
 * Implements parsing job interface to be scheduled and run by quartz executor
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2020-01-28
 */
public class TaskLauncherJob implements ExecutorJob {
    public static final String TASK_ID_KEY = "task";
    public static final String USER_ID_KEY = "user";

    private final TaskLauncher taskLauncher;

    private final WebJobExecutor executor;

    @Inject
    public TaskLauncherJob(TaskLauncher taskLauncher, WebJobExecutor executor) {
        this.taskLauncher = taskLauncher;
        this.executor = executor;
    }

    @Override
    public List<String> getRequiredKeys() {
        return List.of(TASK_ID_KEY, USER_ID_KEY);
    }

    @Override
    public List<String> getAdditionalKeys() {
        return List.of();
    }

    @Override
    public void execute(JobExecutionContext context) {
        var jobData = context.getJobDetail().getJobDataMap();
        int id = jobData.getInt(TASK_ID_KEY);
        int userId = jobData.getInt(USER_ID_KEY);
        try {
            taskLauncher.launch(id, userId);
        } catch (ApplicationException e) {
            executor.unschedule(code(id));
            throw asRuntime(id, userId, e);
        }
    }

    private static RuntimeException asRuntime(int id, int userId, Exception e) {
        var message = "Task [id=" + id + ", userId=" + userId
                + "] doesn't exist anymore. Unscheduled.";
        return ExceptionUtil.asRuntime(e, message);
    }
}
