package ru.job4j.jobseeker.service;

import ru.job4j.jobseeker.dao.UserDao;
import ru.job4j.jobseeker.model.User;

import javax.inject.Inject;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static ru.job4j.jobseeker.service.ValidationHelper.checkNotFoundEntityWithId;

/**
 * Represents service layer of the app (validates the user data and transfers it between web and dao layers)
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2020-01-17
 */
public class UserService {
    private final UserDao dao;

    @Inject
    public UserService(UserDao dao) {
        this.dao = dao;
    }

    /**
     * Asks the dao to find the entity associated with the given id, checks existence of returned one
     *
     * @param id id
     * @return found entity
     */
    public User find(int id) {
        return checkNotFoundEntityWithId(dao.find(id), id);
    }

    /**
     * Asks the dao to give all existing entities
     *
     * @return list of entities
     */
    public List<User> findAll() {
        return dao.findAll();
    }

    /**
     * Asks the dao to create the given entity, checks non-nullity of it before
     *
     * @param user user entity
     */
    public User create(User user) {
        requireNonNull(user, "user must not be null");
        return dao.create(user);
    }

    /**
     * Asks the dao to update the given entity, checks non-nullity of it before
     *
     * @param user user entity
     */
    public void update(User user) {
        requireNonNull(user, "user must not be null");
        checkNotFoundEntityWithId(dao.update(user), user.getId().intValue());
    }

    /**
     * Asks the dao to delete the entity associated with the given id
     * checks if the executed operation was successful (otherwise it throws exception
     *
     * @param id id
     */
    public void delete(int id) {
        checkNotFoundEntityWithId(dao.delete(id), id);
    }
}
