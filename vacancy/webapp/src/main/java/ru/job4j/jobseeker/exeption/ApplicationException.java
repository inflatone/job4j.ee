package ru.job4j.jobseeker.exeption;

/**
 * Represents runtime exception, using to signal that the requested entity is not stored yet
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2020-01-17
 */
public class ApplicationException extends RuntimeException {
    public ApplicationException(String message) {
        super(message);
    }
}