package ru.job4j.ee.store.repository.dbi;

import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import ru.job4j.ee.store.model.User;

import java.util.List;

/**
 * User DAO shell
 * Contains methods to complete CRUD operations with {@link User} objects
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-08
 */
@RegisterConstructorMapper(User.class)
public interface UserDao {
    @GetGeneratedKeys
    @SqlUpdate("INSERT INTO users (login, name, password) VALUES (:login, :name, :password)")
    int insertAndReturnId(@BindBean User user);

    @SqlUpdate("UPDATE users SET login=:login, name=:name, password=:password WHERE id=:id")
    int update(@BindBean User user);

    @SqlUpdate("DELETE FROM users WHERE id=:id")
    int delete(@Bind(value = "id") int id);

    @SqlQuery("SELECT * FROM users WHERE id=:id")
    User find(@Bind(value = "id") int id);

    @SqlQuery("SELECT * FROM users ORDER BY login")
    List<User> findAll();
}