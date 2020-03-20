package ru.job4j.jobseeker.dao;

import one.util.streamex.IntStreamEx;
import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlBatch;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.transaction.Transaction;
import ru.job4j.jobseeker.model.BaseEntity;
import ru.job4j.jobseeker.model.Task;
import ru.job4j.jobseeker.model.Vacancy;
import ru.job4j.vacancy.model.VacancyData;

import java.util.List;
import java.util.Objects;

import static ru.job4j.jobseeker.service.ValidationHelper.checkNotFoundEntityWithId;
import static ru.job4j.jobseeker.service.ValidationHelper.getSafely;

/**
 * User DAO shell
 * Contains methods to complete CRUD operations with {@link Vacancy} objects
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2020-01-20
 */
@RegisterConstructorMapper(Vacancy.class)
public interface VacancyDao {
    @Transaction
    default List<Vacancy> findAll(int taskId, int userId) {
        checkNotFoundEntityWithId(getUserId(taskId) == userId, taskId);
        return findAll(taskId);
    }

    /**
     * Inserts the given vacancy data items into DB, returns the items which already presented in DB
     *
     * @param vacancies vacancy data to save
     * @param task      task
     * @return duplicated vacancies
     */
    @Transaction
    default List<VacancyData> saveAll(List<VacancyData> vacancies, Task task) {
        checkNotFoundEntityWithId(checkUser(task), task.getId().intValue());
        int[] savedRows = saveAllAndReturnAffectedRows(vacancies, task.getId());
        return IntStreamEx.of(savedRows)
                .less(1)
                .mapToObj(vacancies::get)
                .toList();
    }

    @Transaction
    default boolean update(int id, int taskId, int userId, boolean isHighlighted) {
        return checkUser(taskId, userId) && update(id, taskId, isHighlighted);
    }

    @Transaction
    default boolean delete(int id, int taskId, int userId) {
        return checkUser(taskId, userId) && delete(id, taskId);
    }

    @SqlQuery("SELECT * FROM vacancy WHERE task_id=:taskId ORDER BY date DESC")
    List<Vacancy> findAll(@Bind("taskId") int taskId);

    @SqlBatch("INSERT INTO vacancy (task_id, title, description, url, date) " +
            "       VALUES (:taskId, :title, :description, :url, :dateTime) " +
            "  ON CONFLICT (task_id, title) " +
            "DO UPDATE SET description=:description, url=:url, date=:dateTime ")
    int[] saveAllAndReturnAffectedRows(@BindBean List<VacancyData> vacancies, @Bind("taskId") int taskId);

    @SqlUpdate("UPDATE vacancy SET highlighted=:highlighted WHERE id=:id AND task_id=:taskId")
    boolean update(@Bind("id") int id, @Bind("taskId") int taskId, @Bind("highlighted") boolean highlighted);

    @SqlUpdate("DELETE FROM vacancy WHERE id=:id AND task_id=:taskId")
    boolean delete(@Bind("id") int id, @Bind("taskId") int taskId);

    private boolean checkUser(Task task) {
        return checkUser(task.getId(), getSafely(task.getUser(), BaseEntity::getId));
    }

    private boolean checkUser(Integer taskId, Integer userId) {
        return Objects.equals(getUserId(taskId), userId);
    }

    @SqlQuery("SELECT user_id FROM task WHERE id=:id")
    Integer getUserId(@Bind("id") int taskId);
}
