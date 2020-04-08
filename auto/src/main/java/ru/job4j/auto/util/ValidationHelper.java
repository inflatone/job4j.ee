package ru.job4j.auto.util;

import ru.job4j.auto.model.BaseEntity;
import ru.job4j.auto.util.exception.IllegalRequestDataException;
import ru.job4j.auto.util.exception.NotFoundException;

/**
 * Contains utility methods to validate the data from requests
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-12-09
 */
public class ValidationHelper {
    private ValidationHelper() {
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
     * Receives the object, checks if it is not null, and gives it back if okay,
     * throws {@link NotFoundException} with submitted message otherwise
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

    /**
     * Sets the given id to the given entity, if its absent, otherwise checks its equality with the given one
     * Does not use isNew() to prevent entity lazy initialization
     *
     * @param entity entity
     * @param id     id to set (or check)
     */
    public static void assureIdConsistent(BaseEntity entity, int id) {
        // conservative when you reply, but accept liberally
        // http://stackoverflow.com/a/32728226/548473
        Integer entityId = entity.getId();
        if (entityId == null) { // to prevent lazy initialization
            entity.setId(id);
        } else if (entityId != id) {
            throw new IllegalRequestDataException("Wrong entity(id=" + entity.getId() + "), must be with id=" + id);
        }
    }
}
