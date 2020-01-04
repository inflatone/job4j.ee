package ru.job4j.ee.store.repository.dbi;

import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import ru.job4j.ee.store.model.User;

import java.util.List;

import static com.google.common.base.Strings.nullToEmpty;

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
    String SELECT_ALL = "SELECT u.*, t.name city_name, t.country_id city_country_id, c.name city_country_name " +
            "FROM users u LEFT OUTER JOIN city t ON u.city_id = t.id LEFT OUTER JOIN country c on t.country_id = c.id";

    @GetGeneratedKeys
    @SqlUpdate("INSERT INTO users (login, name, password, role, enabled, city_id) VALUES (:login, :name, :password, :role, :enabled, :cityId)")
    int insertAndReturnId(@BindBean User user, @Bind("cityId") Integer cityId);

    @SqlUpdate("UPDATE users SET login=:login, name=:name, password=:password, role=:role, city_id=:cityId WHERE id=:id")
    int update(@BindBean User user, @Bind("cityId") Integer cityId);

    @SqlUpdate("UPDATE users SET login=:login, name=:name, role=:role, city_id=:cityId WHERE id=:id")
    int updateWithoutPassword(@BindBean User user, @Bind("cityId") Integer cityId);

    @SqlUpdate("UPDATE users SET enabled=:enabled WHERE id=:id")
    int enable(@Bind("id") int id, @Bind("enabled") boolean enabled);

    @SqlUpdate("DELETE FROM users WHERE id=:id")
    int delete(@Bind(value = "id") int id);

    @SqlQuery(SELECT_ALL + " WHERE u.id=:id")
    User find(@Bind(value = "id") int id);

    @SqlQuery(SELECT_ALL + " WHERE login=:login")
    User findByLogin(@Bind(value = "login") String login);

    @SqlQuery(SELECT_ALL + " ORDER BY login")
    List<User> findAll();

    default int insertAndReturnId(User user) {
        return insertAndReturnId(user, user.getCity().getId());
    }

    /**
     * @return affected row count
     */
    default int update(User user) {
        return nullToEmpty(user.getPassword()).isBlank() ? updateWithoutPassword(user, user.getCity().getId()) : update(user, user.getCity().getId());
    }
}