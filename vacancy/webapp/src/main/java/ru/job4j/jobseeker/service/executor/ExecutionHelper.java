package ru.job4j.jobseeker.service.executor;

import ru.job4j.jobseeker.model.RepeatRule;
import ru.job4j.jobseeker.model.Task;
import ru.job4j.vacancy.jsoup.ParseParameters;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import static ru.job4j.jobseeker.service.executor.TaskLauncherJob.*;
import static ru.job4j.vacancy.util.TimeUtil.toZonedDateTime;

/**
 * Contains utility methods to handle things related to task executing
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2020-01-28
 */
public class ExecutionHelper {
    private static final String TASK_KEY_TEMPLATE = "Task(id=%d)";

    public static String code(int taskId) {
        return String.format(TASK_KEY_TEMPLATE, taskId);
    }

    public static ParseParameters buildParseParameters(Task task) {
        return ParseParameters.of(Set.of(task.getSource().getTitle()), task.getKeyword(), task.getCity(), toZonedDateTime(task.getLimit()));
    }

    public static Map<String, Integer> buildJobData(Task task) {
        return Map.of(TASK_ID_KEY, task.getId(), USER_ID_KEY, task.getUser().getId());
    }

    /**
     * Builds a cron expression based on task's repeat rule and next launch date
     * (or last execution date if next launch is null)
     *
     * @param task task
     * @return cron expression
     */
    public static String buildCronExpression(Task task) {
        Date launch = task.getLaunch();
        RepeatRule rule = task.getRule();
        return rule.cronExpression(launch != null ? launch : task.getLimit());
    }
}
