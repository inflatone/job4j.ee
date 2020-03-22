package ru.job4j.jobseeker.service;

import ru.job4j.jobseeker.dao.VacancyDao;
import ru.job4j.jobseeker.model.Vacancy;

import javax.inject.Inject;
import java.util.List;

import static ru.job4j.jobseeker.service.ValidationHelper.checkNotFoundEntityWithId;

/**
 * Represents service layer of the app (validates the given vacancy data from the store, then transfers them to the web)
 * Manages the vacancy parsing logic
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2020-01-22
 */
public class VacancyService {
    private final VacancyDao dao;

    @Inject
    public VacancyService(VacancyDao dao) {
        this.dao = dao;
    }

    public List<Vacancy> findAll(int taskId, int userId) {
        // validation goes on in dao layer (JDBI @Transaction could be set only inside dao classes)
        return dao.findAll(taskId, userId);
    }

    public void highlight(int id, int taskId, int userId, boolean isHighlighted) {
        checkNotFoundEntityWithId(dao.update(id, taskId, userId, isHighlighted), id);
    }

    public void delete(int id, int taskId, int userId) {
        checkNotFoundEntityWithId(dao.delete(id, taskId, userId), id);
    }
}