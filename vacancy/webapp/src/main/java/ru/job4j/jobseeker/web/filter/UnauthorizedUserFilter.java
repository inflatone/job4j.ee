package ru.job4j.jobseeker.web.filter;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.job4j.jobseeker.web.security.AuthManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Strings.nullToEmpty;

/**
 * Represents a servlet filter that admits/denies user data requests to the needed authorization part of the app
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2020-01-16
 */
public class UnauthorizedUserFilter extends AbstractRequestFilter {
    private static final String AUTH_PATH = "/login";

    // empty set if no action required
    private static final Map<String, Set<String>> ALLOWED_PATHS_WITH_ACTIONS = Map.of(
            "/profile", Set.of("save"),
            "/ajax", Set.of(),
            "/login", Set.of(),
            "/resources", Set.of(),
            "/webjars", Set.of()
    );

    private final Provider<AuthManager> authManagerProvider;

    @Inject
    public UnauthorizedUserFilter(Provider<AuthManager> managerProvider) {
        this.authManagerProvider = managerProvider;
    }

    @Override
    void act(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getContextPath() + AUTH_PATH);
    }

    @Override
    boolean needAct(HttpServletRequest request) {
        return authManagerProvider.get().isUnauthorized() && isForbiddenPath(request);
    }

    private boolean isForbiddenPath(HttpServletRequest request) {
        var requestPath = request.getRequestURI();
        var action = nullToEmpty(request.getParameter("action"));
        return ALLOWED_PATHS_WITH_ACTIONS.entrySet().stream()
                .noneMatch(entry -> requestPath.contains(entry.getKey()) && checkActionIfRequired(action, entry.getValue()));
    }


    private boolean checkActionIfRequired(String action, Set<String> actions) {
        return actions.isEmpty() // filter path set is empty or action parameter is required
                || actions.contains(action);
    }
}
