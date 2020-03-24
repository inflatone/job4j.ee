package ru.job4j.jobseeker.service;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Trigger;
import ru.job4j.jobseeker.dao.TaskDao;
import ru.job4j.jobseeker.model.RepeatRule;
import ru.job4j.jobseeker.model.ScanSource;
import ru.job4j.jobseeker.model.Task;
import ru.job4j.jobseeker.service.executor.WebJobExecutor;
import ru.job4j.vacancy.util.TimeUtil;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

import static ru.job4j.jobseeker.service.ValidationHelper.checkNotFoundEntityWithId;
import static ru.job4j.jobseeker.service.executor.ExecutionHelper.*;

/**
 * Represents service layer of the app (validates the task data and transfers it between web and dao layers)
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2020-01-17
 */
@Slf4j
public class TaskService {
    private final WebJobExecutor executor;

    private final TaskDao dao;

    @Inject
    public TaskService(WebJobExecutor executor, TaskDao dao) {
        this.executor = executor;
        this.dao = dao;
    }

    public List<ScanSource> findAllSources() {
        return dao.findAllScanSources();
    }

    public List<Task> findAll(int userId) {
        return dao.findAll(userId);
    }

    public Task find(int id, int userId) {
        return checkNotFoundEntityWithId(dao.find(id, userId), id);
    }

    public Task create(Task task, int userId) {
        if (task.getLaunch() == null && task.getRule() != RepeatRule.manually) {
            task.setLaunch(new Date());
        }
        var created = dao.create(task, userId);
        if (task.getRule() != RepeatRule.manually) {
            schedule(task);
        }
        return created;
    }

    public void update(Task task, int userId) {
        Task updated = checkNotFoundEntityWithId(dao.update(task, userId), task.getId());
        if (task.getRule() != RepeatRule.manually || task.getLaunch() != null) {
            reschedule(updated);
        } else {
            unschedule(task.getId());
        }
    }

    public void pause(int taskId, int userId, boolean paused) {
        checkNotFoundEntityWithId(dao.update(taskId, userId, !paused), taskId);
        executor.pause(code(taskId), paused);
    }

    public void delete(int id, int userId) {
        checkNotFoundEntityWithId(dao.delete(id, userId), id);
        unschedule(id);
    }

    public void recount(int userId) {
        dao.recount(userId);
    }

    public void recountNextLaunches(int userId) {
        List<Task> tasks = dao.findAll(userId);
        dao.findAll().stream().filter(t -> t.getRule() != RepeatRule.manually).forEach(t -> t.setLaunch(executor.getNextLaunch(code(t.getId()))));
        dao.innerUpdateAllLaunches(tasks);
    }

    private void schedule(Task task) {
        boolean active = task.isActive();
        Trigger trigger = executor.schedule(code(task.getId()), active, task.getLaunch(),
                buildCronExpression(task), buildJobData(task));
        log.info("Task with id={} scheduled, next start {}", task.getId(), active ? TimeUtil.asLine(trigger.getNextFireTime()) : "paused");
    }

    private void reschedule(Task task) {
        boolean active = task.isActive();
        Trigger trigger = executor.reschedule(code(task.getId()), active, task.getLaunch(), buildCronExpression(task), buildJobData(task));
        log.info("Task with id={} rescheduled, next start {}", task.getId(), active ? TimeUtil.asLine(trigger.getNextFireTime()) : "paused");
    }

    private void unschedule(int taskId) {
        boolean unscheduled = executor.unschedule(code(taskId));
        log.info("Task with id={} {}", taskId, unscheduled ? "has been unscheduled" : "was unscheduled before (or never started)");
    }
}