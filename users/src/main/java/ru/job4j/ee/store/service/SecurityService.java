package ru.job4j.ee.store.service;

import com.google.inject.Inject;
import ru.job4j.ee.store.model.User;
import ru.job4j.ee.store.repository.UserRepository;

import java.util.EnumMap;
import java.util.Map;

/**
 * Represents service layer of the app (validates the security user data from the store, then transfers them to the web)
 * Security service to check authorization credentials
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-11
 */
public class SecurityService {
    @Inject
    private UserRepository repository;

    private static final Map<SecurityCondition, SecurityFilter> FILTERS = new EnumMap<>(SecurityCondition.class) {
        {
            put(SecurityCondition.NOT_FOUND, (expected, actual) -> actual == null);
            put(SecurityCondition.WRONG_PASSWORD, (expected, actual) -> !actual.getPassword().equals(expected.getPassword()));
            put(SecurityCondition.BANNED, (expected, actual) -> !actual.isEnabled());
        }
    };

    /**
     * Asks the store to find the entity associated with the given login,
     * then provides some checks to define whether it's valid or not
     *
     * @param credentials user to to check login and password fields
     * @return found entity
     */
    public User find(User credentials) {
        var user = repository.findByLogin(credentials.getLogin());
        FILTERS.forEach((key, value) -> value.validate(credentials, user, key));
        return user;
    }

    private enum SecurityCondition {
        NOT_FOUND("User with the given login is not registered"),
        WRONG_PASSWORD("Wrong credentials"),
        BANNED("User is banned");

        private final String message;

        SecurityCondition(String message) {
            this.message = message;
        }
    }

    @FunctionalInterface
    private interface SecurityFilter {
        boolean filter(User expected, User actual);

        default void validate(User expected, User actual, SecurityCondition condition) {
            if (filter(expected, actual)) {
                throw new SecurityException(condition.message);
            }
        }
    }
}
