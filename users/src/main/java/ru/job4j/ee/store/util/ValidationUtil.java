package ru.job4j.ee.store.util;

import ru.job4j.ee.store.util.exception.NotFoundException;

/**
 * Contains utility methods to validate the data from requests
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-05
 */
public class ValidationUtil {
    private ValidationUtil() {
        throw new IllegalStateException("should not be instantiated");
    }

    /**
     * Checks and returns (proxy pattern) if the given value is not null,
     * otherwise throws NPE with the message containing the submitted id
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
     * Checks if the given boolean value is true,
     * otherwise throws NPE with the message containing the submitted id
     *
     * @param found given boolean
     * @param id    submitted id
     */
    public static void checkNotFoundEntityWithId(boolean found, int id) {
        checkNotFoundEntity(found, " with id=" + id);
    }

    /**
     * Checks if the given boolean value is true, otherwise throws {@link NotFoundException}, appending the given message to the basic part
     *
     * @param found   given boolean
     * @param message submitted message part
     */
    public static void checkNotFoundEntity(boolean found, String message) {
        checkNotFound(found, "Not found entity" + message);
    }

    /**
     * Checks if the given boolean value is true, otherwise throws {@link NotFoundException} with the given message
     *
     * @param statement given boolean
     * @param message   submitted message
     */
    public static void checkNotFound(boolean statement, String message) {
        if (!statement) {
            throw new NotFoundException(message);
        }
    }
}