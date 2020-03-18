package ru.job4j.jobseeker.service;

import lombok.AllArgsConstructor;
import ru.job4j.jobseeker.dao.UserDao;
import ru.job4j.jobseeker.model.User;

import javax.inject.Inject;
import java.util.EnumMap;
import java.util.Map;

/**
 * Represents service layer of the app (validates the security user data from the store, then transfers them to the web)
 * Security service to check authorization credentials
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2020-01-17
 */
public class SecurityService {
    private static final Map<SecurityCondition, SecurityValidator> FILTERS = new EnumMap<>(SecurityCondition.class) {
        {
            put(SecurityCondition.NOT_FOUND, (user, password) -> user == null);
            put(SecurityCondition.WRONG_PASSWORD, (user, password) -> !user.getPassword().equals(password));
        }
    };

    private final UserDao dao;


    @Inject
    public SecurityService(UserDao dao) {
        this.dao = dao;
    }

    /**
     * Asks the store to find the entity associated with the given login,
     * then provides some checks to define whether it's valid or not
     *
     * @param login    login
     * @param password password
     * @return found entity
     */
    public User find(String login, String password) {
        var user = dao.findByLogin(login);
        FILTERS.forEach((key, value) -> value.validate(user, password, key));
        return user;
    }

    @AllArgsConstructor
    private enum SecurityCondition {
        NOT_FOUND("User with the given login is not registered"),
        WRONG_PASSWORD("Wrong credentials");

        private final String message;
    }

    @FunctionalInterface
    private interface SecurityValidator {
        boolean filter(User actual, String password);

        default void validate(User actual, String password, SecurityCondition condition) {
            if (filter(actual, password)) {
                throw new SecurityException(condition.message);
            }
        }
    }
}
