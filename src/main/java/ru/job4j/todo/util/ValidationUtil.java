package ru.job4j.todo.util;


import ru.job4j.todo.util.exeption.NotFoundException;

public class ValidationUtil {
    private ValidationUtil() {
        // should not instantiate
    }

    public static <T> T checkNotFoundWithId(T object, int id) {
        checkNotFound(object != null, "id=" + id);
        return object;
    }

    public static void checkNotFoundWithId(boolean found, int id) {
        checkNotFound(found, "id=" + id);
    }

    private static void checkNotFound(boolean found, String message) {
        if (!found) {
            throw new NotFoundException("Not found entity with " + message);
        }
    }
}