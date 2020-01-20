package ru.job4j.jobseeker.dao;

import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
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

    @SqlQuery(SELECT_ALL + " WHERE t.id=:id AND t.user_id=:userId")
    Task find(@Bind(value = "id") int id, @Bind(value = "userId") int userId);

    default Task create(Task task, int userId) {
        task.setId(insertAndReturnId(task, task.getSource().getId(), userId));
        task.setUser(new User(userId));
        return task;
    }

    default boolean update(Task task, int userId) {
        return update(task, task.getId(), userId);
    }

    @GetGeneratedKeys
    @SqlUpdate("INSERT INTO task (keyword, city, scan_limit, next_launch, repeat_rule, scan_source_id, user_id) " +
            "        VALUES (:keyword, :city, :limit, :launch, :rule, :sourceId, :userId)")
    int insertAndReturnId(@BindBean Task task, @Bind("sourceId") int sourceId, @Bind("userId") int userId);

    @SqlUpdate("UPDATE task t SET repeat_rule=:rule, next_launch=:launch WHERE t.id=:id AND t.user_id=:userId")
    boolean update(@BindBean Task task, @Bind("id") int id, @Bind("userId") int userId);

    @SqlUpdate("DELETE FROM task WHERE id=:id AND user_id=:userId")
    boolean delete(@Bind("id") int id, @Bind("userId") int userId);
}