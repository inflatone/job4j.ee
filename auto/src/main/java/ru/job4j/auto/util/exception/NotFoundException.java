package ru.job4j.auto.util.exception;

/**
 * Represents runtime exception, using to signal that the requested entity is not stored yet
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2020-02-15
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
