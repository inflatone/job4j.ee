package ru.job4j.ee.store.util;

import com.google.common.base.Strings;

import javax.servlet.ServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Function;

import static java.util.Objects.requireNonNull;

/**
 * Contains general utility methods helping to serve the incoming servlet requests
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-09
 */
public class ServletUtil {
    public static final ThreadLocal<SimpleDateFormat> FORMATTER
            = ThreadLocal.withInitial(() -> new SimpleDateFormat("d.MM.yyyy H:mm:ss"));
    
    private ServletUtil() {
        throw new IllegalStateException("should not be instantiated");
    }

    /**
     * Searches required id parameter inside the request parameter map, throws NPE if cannot find it
     *
     * @param request servlet request
     * @return item id
     */
    public static int getRequiredId(ServletRequest request) {
        return getId(request, true);
    }

    /**
     * Searches id parameter inside the request parameter map, throws NPE if if it's required and not found
     *
     * @param request  request
     * @param required if item id required
     * @return item id
     */
    public static Integer getId(ServletRequest request, boolean required) {
        return getParameter(request, "id", required, Integer::valueOf);
    }

    /**
     * Searches the parameter value associated with the given key inside the given request's parameter map,
     * throws {@link NullPointerException} if it's not found
     *
     * @param request         request
     * @param key             parameter key
     * @param parameterMapper function to extract parameter from its string representation
     * @return parameter value
     */
    public static <T> T getRequiredParameter(ServletRequest request, String key, Function<String, T> parameterMapper) {
        return getParameter(request, key, true, parameterMapper);
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
    public static <T> T getParameter(ServletRequest request, String key, boolean required, Function<String, T> parameterMapper) {
        String parameter = Strings.emptyToNull(request.getParameter(key));
        return required ? parameterMapper.apply(requireNonNull(parameter, key + " must be present"))
                : parameter == null ? null : parameterMapper.apply(parameter);
    }

    /**
     * @param date date
     * @return string representation of the given date
     */
    public static String formatDate(Date date) {
        return FORMATTER.get().format(date);
    }
}