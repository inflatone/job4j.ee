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
    @SqlUpdate("INSERT INTO users (login, name, password, image_id) VALUES (:login, :name, :password, :imageId)")
    int insertAndReturnId(@BindBean User user, @Bind("imageId") Integer imageId);

    @SqlUpdate("UPDATE users SET login=:login, name=:name, password=:password, image_id=:imageId WHERE id=:id")
    int update(@BindBean User user, @Bind("imageId") Integer imageId);

    @SqlUpdate("DELETE FROM users WHERE id=:id")
    int delete(@Bind(value = "id") int id);

    @SqlQuery("SELECT * FROM users WHERE id=:id")
    User find(@Bind(value = "id") int id);

    @SqlQuery("SELECT * FROM users ORDER BY login")
    List<User> findAll();

    default int insertAndReturnId(User user) {
        return insertAndReturnId(user, getImageIdSafely(user));
    }

    /**
     * @return affected row count
     */
    default int update(User user) {
        return update(user, getImageIdSafely(user));
    }

    /**
     * @return ID or null (if entity is null or its id has default value)
     */
    private Integer getImageIdSafely(User user) {
        var image = user.getImage();
        return image == null ? null : zeroToNull(image.getId());
    }

    /**
     * @param id id
     * @return ID or null if id is equals to default value (0)
     */
    private Integer zeroToNull(Integer id) {
        return id == 0 ? null : id;
    }
}