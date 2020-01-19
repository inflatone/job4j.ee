package ru.job4j.jobseeker.web.security;

import ru.job4j.jobseeker.model.Role;
import ru.job4j.jobseeker.model.User;

import javax.servlet.http.HttpServletRequest;
import java.util.function.Function;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;
import static ru.job4j.jobseeker.web.WebHelper.getParameter;

/**
 * Manages the authentication issues
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2020-01-16
 */
public class AuthManager {
    private User user;

    public synchronized User getAuth() {
        return user;
    }

    /**
     * Sets new given user model as auth
     *
     * @param user    user
     * @param replace if auth model should be replaced
     */
    public void setAuth(User user, boolean replace) {
        if (replace || isUnauthorized()) {
            setAuth(user);
        }
    }

    /**
     * Removes the current auth model
     */
    public void removeAuth() {
        setAuth(null);
    }

    /**
     * Removes the current auth model if its id is equal to auth id
     */
    public void removeAuth(int id) {
        var auth = getAuth();
        if (auth != null && auth.getId() == id) {
            removeAuth();
        }
    }

    /**
     * Checks if the manager has auth model
     *
     * @return true if authorized
     */
    public boolean isAuthorized() {
        return getAuth() != null;
    }

    /**
     * Checks if the manager has no auth model
     *
     * @return true if authorized
     */
    public boolean isUnauthorized() {
        return !isAuthorized();
    }

    /**
     * Checks if the manager has admin auth model
     *
     * @return true if authorized
     */
    public boolean isAuthorizedAsAdmin() {
        return getSafely(getAuth(), User::getRole) == Role.ADMIN;
    }

    /**
     * Retrieves id parameter from the given request if only user authorized as admin
     * Otherwise returns the id of the authorized user entity, or null if auth isn't presented
     *
     * @param request request
     * @param key     key of user id parameter in request
     * @return id
     */
    public Integer findAllowedId(HttpServletRequest request, String key) {
        return findAllowedId(() -> getParameter(request, key, Integer::parseInt));
    }

    /**
     * Retrieves user id parameter from the given request if only user authorized as admin
     * Otherwise returns the id of the authorized user entity, or null if auth isn't presented
     *
     * @param request    request
     * @param isUserPage name of user id parameter depends on it
     * @return user id
     */
    public int getRequiredUserId(HttpServletRequest request, boolean isUserPage) {
        return requireNonNull(findAllowedId(request, isUserPage ? "id" : "userId"), "No authorized");
    }

    /**
     * Retrieves id from the given supplier if only user authorized as admin
     * Otherwise returns the id of the authorized user entity, or null if auth isn't presented
     *
     * @param idRetrieverForAdmin supplier of id for admin users
     * @return id
     */
    public Integer findAllowedId(Supplier<Integer> idRetrieverForAdmin) {
        var auth = getAuth();
        var role = getSafely(auth, User::getRole);
        return role == null ? null : findAllowedId(role, idRetrieverForAdmin, auth.getId());
    }

    /**
     * @param role                auth user' role
     * @param idRetrieverForAdmin supplier of id for admin users
     * @param id                  default id value (for no admin users)
     * @return id
     */
    private Integer findAllowedId(Role role, Supplier<Integer> idRetrieverForAdmin, int id) {
        Integer result = null;
        if (role == Role.ADMIN) {
            result = idRetrieverForAdmin.get();
        }
        return result != null ? result : id;
    }

    private synchronized void setAuth(User user) {
        this.user = user;
    }


    /**
     * Helps safely retrieve entity field value in case entity is null
     *
     * @param entity entity
     * @param getter field mapper
     * @return field value or null
     */
    public static <E, T> T getSafely(E entity, Function<E, T> getter) {
        return entity == null ? null : getter.apply(entity);
    }
}
