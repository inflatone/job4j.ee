package ru.job4j.jobseeker.service;

import ru.job4j.jobseeker.exeption.ApplicationException;

import java.util.function.Function;

/**
 * Contains utility methods to validate the data from requests
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2020-01-16
 */
public class ValidationHelper {
    private ValidationHelper() {
        throw new IllegalStateException("should not be instantiated");
    }

    /**
     * Checks and returns (proxy pattern) if a given value is not null,
     * otherwise throws ApplicationException with a message containing the submitted id
     *
     * @param object given value
     * @param id     submitted id
     * @return given value
     */
    public static <T> T checkNotFoundEntityWithId(T object, int id) {
        checkNotFoundEntity(object != null, " with id=" + id);
        return object;
    }

    /**
     * Receives an object, checks if it is not null, and gives it back if okay,
     * throws exception with a submitted message otherwise
     *
     * @param object  given value
     * @param message submitted message
     * @return given value
     */
    public static <T> T checkNotFound(T object, String message) {
        checkNotFound(object != null, message);
        return object;
    }

    /**
     * Checks if a given boolean value is true,
     * otherwise throws exception with a message containing the submitted id
     *
     * @param found given boolean
     * @param id    submitted id
     */
    public static void checkNotFoundEntityWithId(boolean found, int id) {
        checkNotFoundEntity(found, " with id=" + id);
    }

    /**
     * Checks if a given boolean value is true, otherwise throws an exception, appending a given message to a main part
     *
     * @param found   given boolean
     * @param message submitted message part
     */
    public static void checkNotFoundEntity(boolean found, String message) {
        checkNotFound(found, "Not found entity" + message);
    }

    /**
     * Checks if a given boolean value is true, otherwise throws an exception with a given message
     *
     * @param statement given boolean
     * @param message   submitted message
     */
    public static void checkNotFound(boolean statement, String message) {
        check(statement, true, message);
    }

    /**
     * Checks if a given boolean value is equal to expected one, otherwise throws an exception with a given message
     *
     * @param statement given boolean
     * @param expected  expected boolean
     * @param message   submitted message
     */
    public static void check(boolean statement, boolean expected, String message) {
        if (statement != expected) {
            throw new ApplicationException(message);
        }
    }

    /**
     * Helps safely retrieve entity field value in case entity is null
     *
     * @param entity entity
     * @param getter field mapper
     * @return field value or null
     */
    public static <E, T> T getSafely(E entity, Function<E, T> getter) {
        return entity == null ? null : getter.apply(entity);
    }
}
