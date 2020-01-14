package ru.job4j.vacancy.util;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Contains util methods to handle exception throwing functions
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @see <a href="https://stackoverflow.com/a/27252163/10375242">https://stackoverflow.com/a/27252163/10375242</a>
 * @since 2020-01-15
 */
public class ExceptionUtil {
    public static void handleRun(RunnableEx runnable) {
        runnable.run();
    }

    public static <T> void handleAccept(T value, ConsumerEx<T> consumer) {
        consumer.accept(value);
    }

    public static <T> T handleGet(SupplierEx<T> supplier) {
        return supplier.get();
    }

    public static <T, R> R handleApply(T value, FunctionEx<T, R> function) {
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
            return handleApply(null, t -> getEx());
        }

        T getEx() throws Exception;
    }

    @FunctionalInterface
    public interface ConsumerEx<T> extends Consumer<T> {
        @Override
        default void accept(T t) {
            handleRun(() -> acceptEx(t));
        }

        void acceptEx(T t) throws Exception;
    }

    @FunctionalInterface
    public interface RunnableEx extends Runnable {
        @Override
        default void run() {
            handleApply(null,
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
    public static IllegalStateException asRuntime(Throwable e) {
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
    public static IllegalStateException asRuntime(Throwable e, String message) {
        return new IllegalStateException(message, e);
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
}
