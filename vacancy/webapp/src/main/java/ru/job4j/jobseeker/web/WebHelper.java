package ru.job4j.jobseeker.web;

import com.google.common.base.Strings;
import ru.job4j.jobseeker.web.json.JsonHelper;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

public class WebHelper {

    private WebHelper() {
        throw new IllegalStateException("should not be instantiated");
    }

    public static <T> T getParameter(ServletRequest request, String key, Function<String, T> parameterMapper) {
        return getParameter(request, key, false, parameterMapper);
    }

    public static String getParameter(ServletRequest request, String key) {
        return getParameter(request, key, Function.identity());
    }

    public static <T> T getRequiredParameter(ServletRequest request, String key, Function<String, T> parameterMapper) {
        return getParameter(request, key, true, parameterMapper);
    }

    public static String getRequiredParameter(ServletRequest request, String key) {
        return getRequiredParameter(request, key, Function.identity());
    }

    /**
     * Searches the parameter value associated with the given key inside the given request's parameter map,
     * throws {@link NullPointerException} if it's set required and not found
     *
     * @param request         request
     * @param key             parameter key
     * @param required        if parameter existence is certainly required
     * @param parameterMapper function to extract parameter from its string representation
     * @return parameter value
     */
    private static <T> T getParameter(ServletRequest request, String key, boolean required, Function<String, T> parameterMapper) {
        String parameter = Strings.emptyToNull(request.getParameter(key));
        return required ? parameterMapper.apply(requireNonNull(parameter, key + " must be present"))
                : parameter == null ? null : parameterMapper.apply(parameter);
    }

    // http://stackoverflow.com/a/28565320/548473
    public static Throwable getRootCause(Throwable t) {
        Throwable result = t;
        Throwable cause;
        while (null != (cause = result.getCause()) && result != cause) {
            result = cause;
        }
        return result;
    }

    public static <T> T suppressException(Callable<T> function, T defaultValue) {
        try {
            return function.call();
        } catch (Exception e) {
            return defaultValue;
        }
    }

    /**
     * Writes the given key-value pair to response as JSON
     *
     * @param response response
     * @param key      key
     * @param value    value
     */
    public static <K, V> void asJsonToResponse(HttpServletResponse response, K key, V value) throws IOException {
        var json = JsonHelper.asJson(key, value);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }

    /**
     * Writes the given entity to response as JSON
     *
     * @param response response
     * @param obj      entity
     */
    public static <T> void asJsonToResponse(HttpServletResponse response, T obj) throws IOException {
        var json = JsonHelper.asJson(obj);
        response.setContentType("application/json");
        response.getWriter().write(json);
    }

    /**
     * Writes the given error message to response as JSON
     *
     * @param response response
     * @param message  error message
     */
    public static void errorAsJsonToResponse(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        asJsonToResponse(response, "error", message);
    }
}