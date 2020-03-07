package ru.job4j.ee.store.web.auth;

import org.slf4j.Logger;
import ru.job4j.ee.store.model.Role;
import ru.job4j.ee.store.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.job4j.ee.store.util.ServletUtil.getParameter;
import static ru.job4j.ee.store.util.ValidationUtil.checkNotFound;

/**
 * Contains utility methods to serve auth operations
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-11
 */
public class AuthUtil {
    private static final Logger log = getLogger(AuthUtil.class);

    private AuthUtil() {
        throw new IllegalStateException("should not be instantiated");
    }

    /**
     * Retrieves the auth user data model via the request (retrieves session object from it),
     * if required set, checks if result exists (otherwise it throws {@link ru.job4j.ee.store.util.exception.NotFoundException}
     *
     * @param request  request
     * @param required if required or not
     * @return auth user
     */
    public static User getAuthUser(HttpServletRequest request, boolean required) {
        var session = request.getSession(false);
        var user = session == null ? null : (User) session.getAttribute("authUser");
        return required ? checkNotFound(user, "No auth data found") : user;
    }

    /**
     * Sets the given user object as authorized user in the http session object
     *
     * @param request  request
     * @param authUser user model
     */
    public static void setAuthorizedUser(HttpServletRequest request, User authUser) {
        log.info("set auth as {}", authUser);
        var session = request.getSession();
        session.setAttribute("authUser", authUser);
    }

    /**
     * Sets the given user object as authorized user in the absence of authentication presently
     *
     * @param request request
     * @param user    user model
     */
    public static void setAuthorizedIfUnauthorized(HttpServletRequest request, User user) {
        var authUser = getAuthUser(request, false);
        if (authUser == null) {
            setAuthorizedUser(request, user);
        }
    }

    /**
     * Removes the auth user data from the http session object
     *
     * @param request request
     */
    public static void setUnauthorizedUser(HttpServletRequest request) {
        request.getSession().invalidate();
    }

    /**
     * Removes the auth user data info from the http session object if the auth user's id is equals to the given user id
     *
     * @param request request
     * @param userId  id
     */
    public static void setUnauthorizedIfSameId(HttpServletRequest request, int userId) {
        var authUser = getAuthUser(request, true);
        if (authUser.getId() == userId) {
            setUnauthorizedUser(request);
        }
    }

    /**
     * Checks if the given user object has an ADMIN role
     *
     * @param request request
     * @return {@code true} if ADMIN
     */
    public static boolean isAdminAuth(HttpServletRequest request) {
        var user = getAuthUser(request, false);
        return user != null && user.getRole() == Role.ADMIN;
    }

    /**
     * Retrieves the parameter from the given request's context if user has ADMIN role,
     * returns null otherwise
     *
     * @param request request
     * @return user id
     */
    public static <T> T retrieveIfAdminOrCheckSession(HttpServletRequest request, String key, Function<String, T> parameterMapper) {
        var authUser = getAuthUser(request, true);
        return authUser.getRole() != Role.ADMIN ? null
                : getParameter(request, key, false, parameterMapper);
    }

    /**
     * Sends redirection to the main page (depending on which role the auth user has)
     *
     * @param request  request
     * @param response response
     */
    public static void redirectToMain(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getContextPath() + '/' + redirectTo(request));
    }

    /**
     * Returns the main page name depending on which role the given auth user has
     *
     * @param request request
     * @return a page name to be redirected to
     */
    public static String redirectTo(HttpServletRequest request) {
        return isAdminAuth(request) ? "users" : "profile";
    }

    /**
     * Retrieves the auth user data from the session object,
     * then composes the roles to send on the front-side (depending on which role the auth user has)
     *
     * @param request request
     * @return set of app roles
     */
    public static Set<Role> composeAvailableRoles(HttpServletRequest request) {
        var user = getAuthUser(request, false);
        var role = Optional.ofNullable(user).map(User::getRole).orElse(Role.USER);
        return role == Role.ADMIN ? Set.of(Role.values()) : Set.of(role);
    }

    /**
     * Sets the forbidden code to the given response
     *
     * @param response response
     */
    public static void sendForbidden(HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "You're not authorized to pass here");
    }

    /**
     * Unwraps the root cause of the given throwable object
     *
     * @param t throwable obj
     * @return root obj
     */
    // http://stackoverflow.com/a/28565320/548473
    public static Throwable getRootCause(Throwable t) {
        Throwable result = t;
        Throwable cause;
        while (null != (cause = result.getCause()) && result != cause) {
            result = cause;
        }
        return result;
    }
}