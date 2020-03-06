package ru.job4j.ee.store.repository;

import ru.job4j.ee.store.model.User;
import ru.job4j.ee.store.repository.dbi.JdbiProvider;
import ru.job4j.ee.store.repository.dbi.UserDao;

import java.util.List;

/**
 * Represents user DB storage accessor
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-08
 */
public class JdbiUserRepository implements UserRepository {
    private static final UserRepository INSTANCE_HOLDER = new JdbiUserRepository();

    private final UserDao dao;

    public static UserRepository getUserRepository() {
        return INSTANCE_HOLDER;
    }

    private JdbiUserRepository() {
        this.dao = JdbiProvider.getUserDao();
    }

    @Override
    public boolean save(User user) {
        if (user.isNew()) {
            user.setId(dao.insertAndReturnId(user));
            return true;
        }
        return dao.update(user) != 0;
    }

    @Override
    public boolean delete(int id) {
        return dao.delete(id) != 0;
    }

    @Override
    public boolean enable(int id, boolean enabled) {
        return dao.enable(id, enabled) != 0;
    }

    @Override
    public User find(int id) {
        return dao.find(id);
    }

    @Override
    public User findByLogin(String login) {
        return dao.findByLogin(login);
    }

    @Override
    public List<User> findAll() {
        return dao.findAll();
    }
}
