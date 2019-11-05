package ru.job4j.ee.store.service;

import ru.job4j.ee.store.model.User;
import ru.job4j.ee.store.repository.UserRepository;
import ru.job4j.ee.store.util.ValidationUtil;

import java.util.List;

import static java.util.Objects.requireNonNull;
import static ru.job4j.ee.store.repository.InMemoryUserRepository.getUserRepository;
import static ru.job4j.ee.store.util.ValidationUtil.checkNotFoundEntityWithId;

/**
 * Represents service layer of the app (validates the given data from the store, then transfers them to the web)
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

    private UserService() {
    }

    /**
     * Asks the store to create the given entity, checks non-nullity of it
     *
     * @param user user entity
     * @return resulting entity returned from the store after insert
     */
    public User create(User user) {
        requireNonNull(user, "user must not be null");
        return repository.save(user);
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
        checkNotFoundEntityWithId(repository.save(user), user.getId());
    }

    /**
     * Asks the store to delete the entity associated with the given id
     * checks if the executed operation was successful (otherwise it throws {@link ru.job4j.ee.store.util.exception.NotFoundException})
     *
     * @param id id
     */
    public void delete(int id) {
        ValidationUtil.checkNotFoundEntityWithId(repository.delete(id), id);
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
