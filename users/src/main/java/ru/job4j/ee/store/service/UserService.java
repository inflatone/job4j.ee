package ru.job4j.ee.store.service;

import ru.job4j.ee.store.model.User;
import ru.job4j.ee.store.repository.CityRepository;
import ru.job4j.ee.store.repository.UserRepository;

import java.util.List;

import static java.util.Objects.requireNonNull;
import static ru.job4j.ee.store.repository.JdbiCityRepository.getCityRepository;
import static ru.job4j.ee.store.repository.JdbiUserRepository.getUserRepository;
import static ru.job4j.ee.store.util.ValidationUtil.checkNotFoundEntityWithId;

/**
 * Represents service layer of the app (validates the given user data from the store, then transfers them to the web)
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-05
 */
public class UserService {
    private static final UserService INSTANCE_HOLDER = new UserService();

    public static UserService getUserService() {
        return INSTANCE_HOLDER;
    }

    private final UserRepository repository = getUserRepository();

    private final CityRepository cityRepository = getCityRepository();

    private UserService() {
    }

    /**
     * Asks the store to create the given entity, checks non-nullity of it
     *
     * @param user user entity
     */
    public void create(User user) {
        requireNonNull(user, "user must not be null");
        cityRepository.save(
                requireNonNull(user.getCity(), "city must not be null"));
        repository.save(user);
    }

    /**
     * Asks the store  to find the entity associated with the given id, checks not-nullity of returned one
     *
     * @param id id
     * @return found entity
     */
    public User find(int id) {
        return checkNotFoundEntityWithId(repository.find(id), id);
    }

    /**
     * Asks the store to update the given entity, checks non-nullity of it
     *
     * @param user user entity
     */
    public void update(User user) {
        requireNonNull(user, "user must not be null");
        int id = user.getId();
        cityRepository.save(
                requireNonNull(user.getCity(), "city must not be null"));
        checkNotFoundEntityWithId(repository.save(user), id);
    }

    /**
     * Asks the store to delete the entity associated with the given id
     * checks if the executed operation was successful (otherwise it throws {@link ru.job4j.ee.store.util.exception.NotFoundException})
     *
     * @param id id
     */
    public void delete(int id) {
        checkNotFoundEntityWithId(repository.delete(id), id);
    }

    /**
     * Asks the store to enable/disable the given entity, checks if successful of the executed operation
     *
     * @param id      id
     * @param enabled enable/disable point
     */
    public void enable(int id, boolean enabled) {
        checkNotFoundEntityWithId(repository.enable(id, enabled), id);
    }

    /**
     * Asks the store to give all existing entities
     *
     * @return list of entities
     */
    public List<User> findAll() {
        return repository.findAll();
    }
}