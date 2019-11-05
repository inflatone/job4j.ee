package ru.job4j.ee.store.repository;

import one.util.streamex.StreamEx;
import ru.job4j.ee.store.model.User;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents user in-memory storage
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-05
 */
public class InMemoryUserRepository implements UserRepository {
    private static final AtomicInteger INT_SEQ = new AtomicInteger(0);
    private static final UserRepository INSTANCE_HOLDER = new InMemoryUserRepository();

    public static UserRepository getUserRepository() {
        return INSTANCE_HOLDER;
    }

    private final Map<Integer, User> storage = new ConcurrentHashMap<>();

    private InMemoryUserRepository() {
    }

    @Override
    public User save(User user) {
        if (user.isNew()) {
            user.setId(INT_SEQ.incrementAndGet());
            storage.put(user.getId(), new User(user));
        } else {
            if (storage.computeIfPresent(user.getId(), (id, oldUser) -> {
                user.setCreated(oldUser.getCreated());
                return new User(user);
            }) == null) {
                return null; // not found to update
            }
        }
        return user;
    }

    @Override
    public boolean delete(int id) {
        return storage.remove(id) != null;
    }

    @Override
    public User find(int id) {
        return new User(storage.get(id));
    }

    @Override
    public List<User> findAll() {
        return StreamEx.of(storage.values())
                .map(User::new)
                .sortedBy(User::getLogin)
                .toList();
    }
}
