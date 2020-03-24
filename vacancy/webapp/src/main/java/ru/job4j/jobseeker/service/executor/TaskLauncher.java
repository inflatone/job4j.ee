package ru.job4j.jobseeker.service.executor;

import ru.job4j.jobseeker.model.LaunchLog;

/**
 * Represents an interface that describes the behaviour of task launcher inside executor
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2020-01-28
 */
@FunctionalInterface
public interface TaskLauncher {
    /**
     * Asks stored data of a task associated with the given id and user id,
     * then prepares and launches the task execution according to the received task data,
     * creates and fills a launch log object as result
     *
     * @param id     task id
     * @param userId user id
     * @return launch log
     */
    LaunchLog launch(int id, int userId);
}