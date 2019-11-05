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
}