package ru.job4j.ee.store.repository;

import ru.job4j.ee.store.model.User;

/**
 * Represents interface to implement user data storing classes
 * In future may contain additional methods required to process user TOs
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-05
 */
public interface UserRepository extends Repository<User> {
    /**
     * Finds the entity associated with the given login in the store
     *
     * @param login user login
     * @return found entity, or null if not found
     */
    User findByLogin(String login);

    /**
     * Enables or disables user profile associated with the given id
     *
     * @param id      id
     * @param enabled enable/disable point
     */
    boolean enable(int id, boolean enabled);

    @Override
    default int clear() {
        throw new UnsupportedOperationException("not implemented");
    }
}