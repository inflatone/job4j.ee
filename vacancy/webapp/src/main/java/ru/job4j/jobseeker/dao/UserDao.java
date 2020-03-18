package ru.job4j.jobseeker.dao;

import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import ru.job4j.jobseeker.model.User;

import java.util.List;

import static com.google.common.base.Strings.nullToEmpty;

/**
 * User DAO shell
 * Contains methods to complete CRUD operations with {@link User} objects
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2020-01-17
 */
@RegisterConstructorMapper(User.class)
public interface UserDao {
    String SELECT_ALL = "SELECT * FROM users u";

    @SqlQuery(SELECT_ALL + " WHERE u.id=:id")
    User find(@Bind(value = "id") int id);

    @SqlQuery(SELECT_ALL + " WHERE login=:login")
    User findByLogin(@Bind(value = "login") String login);

    @SqlQuery(SELECT_ALL + " ORDER BY login")
    List<User> findAll();


    default User create(User user) {
        user.setId(insertAndReturnId(user));
        return user;
    }

    default boolean update(User user) {
        return (nullToEmpty(user.getPassword()).isBlank() ? updateWithoutPassword(user) : updateWithPassword(user)) != 0;
    }

    @SqlUpdate("DELETE FROM users WHERE id=:id")
    boolean delete(@Bind(value = "id") int id);

    @GetGeneratedKeys
    @SqlUpdate("INSERT INTO users (login, password, role) VALUES (:login, :password, :role)")
    int insertAndReturnId(@BindBean User user);

    @SqlUpdate("UPDATE users SET login=:login, password=:password, role=:role WHERE id=:id")
    int updateWithPassword(@BindBean User user);

    @SqlUpdate("UPDATE users SET login=:login, role=:role WHERE id=:id")
    int updateWithoutPassword(@BindBean User user);
}
