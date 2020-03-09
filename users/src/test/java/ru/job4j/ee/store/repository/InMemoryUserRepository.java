package ru.job4j.ee.store.repository;

import one.util.streamex.StreamEx;
import ru.job4j.ee.store.model.User;
import ru.job4j.ee.store.model.UserImage;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Strings.nullToEmpty;

/**
 * Represents user in-memory storage
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-05
 */
public class InMemoryUserRepository extends InMemoryStorage<User> implements UserRepository {
    private Map<String, User> loginStorage = new ConcurrentHashMap<>();

    @Override
    public boolean save(User user) {
        var modelToPersist = new User(user); // use standalone obj to store
        if (checkNew(modelToPersist)) {
            int id = SEQ.incrementAndGet();
            modelToPersist.setId(id);
            storage.put(id, modelToPersist);
            user.setId(id);
            return true;
        }
        return storage.computeIfPresent(user.getId(), (id, oldUser) -> {
            if (nullToEmpty(user.getPassword()).isBlank()) {
                user.setPassword(oldUser.getPassword());
            }
            user.setCreated(oldUser.getCreated());
            return new User(user);
        }) != null; // null -> not found to update
    }

    @Override
    public boolean delete(int id) {
        var user = storage.get(id);
        return user != null && loginStorage.remove(user.getLogin()) != null && storage.remove(id) != null;
    }

    @Override
    public User find(int id) {
        var user = storage.get(id);
        return user == null ? null : makeCopy(user);
    }

    @Override
    public User findByLogin(String login) {
        var user = loginStorage.get(login);
        return user == null ? null : makeCopy(user);
    }

    @Override
    public boolean enable(int id, boolean enabled) {
        return storage.computeIfPresent(id, (i, u) -> {
            u.setEnabled(enabled);
            return u;
        }) != null; // null -> not found to update
    }

    @Override
    public List<User> findAll() {
        return StreamEx.of(storage.values())
                .map(this::makeCopy)
                .sortedBy(User::getLogin)
                .toList();
    }

    private boolean checkNew(User user) {
        if (user.isNew()) {
            loginStorage.compute(user.getLogin(), (i, u) -> {
                if (u != null) {
                    throw new RuntimeException(new SQLException()); // simulate db behavior on duplicate login insert
                }
                return user;
            });
            return true;
        }
        return false;
    }

    // Imitate logic of JDBI profile
    private User makeCopy(User persisted) {
        var user = new User(persisted);
        if (user.getImage() == null) {
            user.setImage(new UserImage(0));
        }
        return user;
    }
}
