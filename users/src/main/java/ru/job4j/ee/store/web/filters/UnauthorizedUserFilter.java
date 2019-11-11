package ru.job4j.ee.store.web.filters;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static ru.job4j.ee.store.web.auth.AuthUtil.getAuthUser;

/**
 * Represents a servlet filter that admits/denies user data requests to the needed authorization part of the app
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-11
 */
public class UnauthorizedUserFilter extends AbstractRequestFilter {
    private static final String AUTH_PATH = "/login";

    // empty set if no action required
    private static final Map<String, Set<String>> ALLOWED_PATHS_WITH_ACTIONS = Map.of(
            "/profile", Set.of("create"),
            "/login", Set.of(),
            "/resources", Set.of()
    );

    public UnauthorizedUserFilter() {
        super(UnauthorizedUserFilter::hasNoAuthAndRequestsForbiddenPath, UnauthorizedUserFilter::sendRedirectToAuthPage);
    }

    private static boolean hasNoAuthAndRequestsForbiddenPath(HttpServletRequest request) {
        return getAuthUser(request, false) == null && isForbiddenPath(request);
    }

    private static void sendRedirectToAuthPage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getContextPath() + AUTH_PATH);
    }

    private static boolean isForbiddenPath(HttpServletRequest request) {
        var requestPath = request.getRequestURI();
        var action = request.getParameter("action");
        return ALLOWED_PATHS_WITH_ACTIONS.entrySet().stream()
                .noneMatch(entry -> requestPath.contains(entry.getKey()) && checkActionIfRequired(action, entry.getValue()));
    }


    private static boolean checkActionIfRequired(String action, Set<String> actions) {
        return actions.isEmpty() // filter path set is empty or action parameter is required
                || action != null && actions.contains(action); // contains() throws NPE if null given
    }
}