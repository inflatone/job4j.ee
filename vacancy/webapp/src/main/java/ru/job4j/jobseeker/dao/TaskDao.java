package ru.job4j.jobseeker.dao;

import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlBatch;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.jdbi.v3.sqlobject.transaction.Transaction;
import ru.job4j.jobseeker.model.LaunchLog;
import ru.job4j.jobseeker.model.ScanSource;
import ru.job4j.jobseeker.model.Task;
import ru.job4j.jobseeker.model.User;

import java.util.List;

/**
 * User DAO shell
 * Contains methods to complete CRUD operations with {@link Task} objects
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2020-01-17
 */
@RegisterConstructorMapper(Task.class)
public interface TaskDao {
    String SELECT_ALL =
            "      SELECT t.*, ss.code scan_source_code, ss.url scan_source_url, ss.icon_url scan_source_icon_url " +
                    "FROM task t " +
                    "LEFT OUTER JOIN scan_source ss on ss.id = t.scan_source_id ";

    @RegisterConstructorMapper(ScanSource.class)
    @SqlQuery("SELECT * FROM scan_source ORDER BY code")
    List<ScanSource> findAllScanSources();

    @SqlQuery(SELECT_ALL + " WHERE t.user_id=:userId ORDER BY t.keyword, t.city")
    List<Task> findAll(@Bind(value = "userId") int userId);

    @SqlQuery(SELECT_ALL)
    List<Task> findAll();

    @SqlQuery(SELECT_ALL + " WHERE t.id=:id AND t.user_id=:userId")
    Task find(@Bind(value = "id") int id, @Bind(value = "userId") int userId);

    default Task create(Task task, int userId) {
        task.setId(insertAndReturnId(task, task.getSource().getId(), userId));
        task.setUser(new User(userId));
        return task;
    }

    @Transaction
    default Task update(Task task, int userId) {
        return update(task, task.getId(), userId) ? find(task.getId(), userId) : null;
    }

    @SqlUpdate("UPDATE task SET scan_limit=:limit, next_launch=:launch, amount=:amount WHERE id=:id")
    void innerUpdate(@BindBean Task task);

    @SqlBatch("UPDATE task SET next_launch=:launch WHERE id=:id")
    void innerUpdateAllLaunches(@BindBean List<Task> tasks);

    @SqlUpdate("UPDATE task SET active=:active WHERE id=:id AND user_id=:userId")
    boolean update(@Bind("id") int id, @Bind("userId") int userId, @Bind("active") boolean active);

    @GetGeneratedKeys
    @SqlUpdate("INSERT INTO task (keyword, city, scan_limit, active, next_launch, repeat_rule, scan_source_id, user_id) " +
            "        VALUES (:keyword, :city, :limit, :active, :launch, :rule, :sourceId, :userId)")
    int insertAndReturnId(@BindBean Task task, @Bind("sourceId") int sourceId, @Bind("userId") int userId);

    @SqlUpdate("UPDATE task t SET active=:active, repeat_rule=:rule, scan_limit=:limit, next_launch=:launch WHERE t.id=:id AND t.user_id=:userId")
    boolean update(@BindBean Task task, @Bind("id") int id, @Bind("userId") int userId);

    @SqlUpdate("INSERT INTO launch_log(date_time, found_amount, added_amount, status, task_id) VALUES (:dateTime, :foundAmount, :addedAmount, :status, :taskId)")
    void saveLog(@BindBean LaunchLog log, @Bind("taskId") int taskId);

    @SqlUpdate("DELETE FROM task WHERE id=:id AND user_id=:userId")
    boolean delete(@Bind("id") int id, @Bind("userId") int userId);

    @SqlUpdate("UPDATE task t SET amount = (SELECT count (v.id) FROM vacancy v WHERE v.task_id = t.id ) WHERE t.user_id=:id")
    void recount(@Bind("id") int id);
}