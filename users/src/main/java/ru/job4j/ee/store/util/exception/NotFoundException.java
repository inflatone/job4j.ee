package ru.job4j.ee.store.util.exception;

/**
 * Represents runtime exception, using to signal that the requested entity is not stored yet
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-05
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
