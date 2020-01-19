package ru.job4j.jobseeker.service;

import ru.job4j.jobseeker.dao.TaskDao;
import ru.job4j.jobseeker.model.ScanSource;
import ru.job4j.jobseeker.model.Task;

import javax.inject.Inject;
import java.util.List;

import static ru.job4j.jobseeker.service.ValidationHelper.checkNotFoundEntityWithId;

/**
 * Represents service layer of the app (validates the task data and transfers it between web and dao layers)
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2020-01-17
 */
public class TaskService {
    private final TaskDao dao;

    @Inject
    public TaskService(TaskDao dao) {
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
        return dao.create(task, userId);
    }

    public void update(Task task, int userId) {
        checkNotFoundEntityWithId(dao.update(task, userId), task.getId().intValue());
    }

    public void delete(int id, int userId) {
        checkNotFoundEntityWithId(dao.delete(id, userId), id);
    }
}