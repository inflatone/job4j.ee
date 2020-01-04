package ru.job4j.ee.store.util;

import com.google.common.base.Strings;

import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.Callable;
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
    private static final String PREFIX = "WEB-INF/jsp/";
    private static final String SUFFIX = ".jsp";
    private static final String REDIRECT_FORMAT = PREFIX + "%s" + SUFFIX;

    private ServletUtil() {
        throw new IllegalStateException("should not be instantiated");
    }

    /**
     * Sets the given page name to the redirect formatter ("WEB-INF/jsp/*.jsp")
     *
     * @param jspName page name
     * @return JSP path to redirect
     */
    public static String createRedirection(String jspName) {
        return String.format(REDIRECT_FORMAT, jspName);
    }

    /**
     * Includes the logic of forwarding the given request to JSP path associated with the given JSP name
     *
     * @param jspName  page name
     * @param request  request
     * @param response response
     */
    public static void forwardToJsp(String jspName, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        var url = createRedirection(jspName);
        request.getRequestDispatcher(url).forward(request, response);
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
     * Suppresses an exception, which may be thrown due function computation (and returns the given default value in this case)
     *
     * @param function function to run
     * @return result of function computation, or the default value if an exception would be thrown
     */
    public static <T> T suppressException(Callable<T> function, T defaultValue) {
        try {
            return function.call();
        } catch (Exception e) {
            return defaultValue;
        }
    }
}