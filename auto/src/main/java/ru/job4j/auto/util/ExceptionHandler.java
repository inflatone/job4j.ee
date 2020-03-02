package ru.job4j.auto.util;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.google.common.base.Throwables.getRootCause;

public class ExceptionHandler {
    public static void executeEx(RunnableEx runnable) {
        runnable.run();
    }

    public static <T> void consumeEx(T value, ConsumerEx<T> consumer) {
        consumer.accept(value);
    }

    public static <T> T produceEx(SupplierEx<T> supplier) {
        return supplier.get();
    }

    public static <T, R> R mapEx(T value, FunctionEx<T, R> function) {
        return function.apply(value);
    }

    @FunctionalInterface
    public interface FunctionEx<T, R> extends Function<T, R> {
        @Override
        default R apply(T t) {
            try {
                return applyEx(t);
            } catch (Exception e) {
                throw asRuntime(e);
            }
        }

        R applyEx(T t) throws Exception;
    }


    @FunctionalInterface
    public interface SupplierEx<T> extends Supplier<T> {
        @Override
        default T get() {
            return mapEx(null, t -> getEx());
        }

        T getEx() throws Exception;
    }

    @FunctionalInterface
    public interface ConsumerEx<T> extends Consumer<T> {
        @Override
        default void accept(T t) {
            executeEx(() -> acceptEx(t));
        }

        void acceptEx(T t) throws Exception;
    }

    @FunctionalInterface
    public interface RunnableEx extends Runnable {
        @Override
        default void run() {
            mapEx(null,
                    t -> {
                        runEx();
                        return null;
                    });
        }

        void runEx() throws Exception;
    }

    /**
     * Wraps a given exception into an IllegalStateException instance, composes the message
     *
     * @param e exception
     * @return runtime exception
     */
    public static RuntimeException asRuntime(Throwable e) {
        return asRuntime(e, "Error: " + asLine(e));
    }

    /**
     * Retrieves a message from a given exception object,
     * sends a class name instead if the message's absent
     *
     * @param e exception
     * @return message
     */
    public static String asLine(Throwable e) {
        var message = e.getMessage();
        return (message != null ? message : e.getClass().getSimpleName());
    }

    /**
     * Wraps the given exception into an IllegalStateException instance, submits to it the given message
     *
     * @param e exception
     * @return runtime exception
     */
    public static RuntimeException asRuntime(Throwable e, String message) {
        return new RuntimeException(message, e);
    }

    /**
     * Helps safely retrieve entity field value in case entity is null, avoiding to throw NullPointerException
     *
     * @param entity entity
     * @param getter field mapper
     * @return field value or null
     */
    public static <E, T> T nullSafely(E entity, FunctionEx<E, T> getter) {
        return entity == null ? null : getter.apply(entity);
    }

    public static Throwable logAndGetRootCause(
            Logger log, HttpServletRequest request, Throwable e, boolean logException, HttpStatus status) {
        Throwable rootCause = getRootCause(e);
        if (logException) {
            log.error(status + " at request " + request.getRequestURL(), rootCause);
        } else {
            log.warn("{} at request {}: {}", status, request.getRequestURL(), rootCause.toString());
        }
        return rootCause;
    }

    public static String getMessage(Throwable e) {
        var localizedMessage = e.getLocalizedMessage();
        return localizedMessage != null ? localizedMessage : e.getClass().getName();
    }
}
