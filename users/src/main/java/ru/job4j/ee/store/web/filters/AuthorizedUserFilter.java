package ru.job4j.ee.store.web.filters;

import ru.job4j.ee.store.web.auth.AuthUtil;

import javax.servlet.http.HttpServletRequest;

import static ru.job4j.ee.store.web.auth.AuthUtil.getAuthUser;

/**
 * Represents an additional servlet filter that admits/denies user data requests to the 'CREATE USER' section of the app
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-11
 */
public class AuthorizedUserFilter extends AbstractRequestFilter {

    public AuthorizedUserFilter() {
        super(AuthorizedUserFilter::isCreateActionWhenAuthorized,
                AuthUtil::redirectToMain);
    }

    /**
     * @param request request
     * @return {@code true} if create action chosen when user already authorized
     */
    private static boolean isCreateActionWhenAuthorized(HttpServletRequest request) {
        return "create".equals(request.getParameter("action")) && getAuthUser(request, false) != null;
    }
}