package ru.job4j.ee.store.web.filters;

import static ru.job4j.ee.store.web.auth.AuthUtil.isAdminAuth;
import static ru.job4j.ee.store.web.auth.AuthUtil.sendForbidden;

/**
 * Represents a servlet filter that admits/denies user data requests to the admin part of the app
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-11
 */
public class AdminUserFilter extends AbstractRequestFilter {
    public AdminUserFilter() {
        super(request -> !isAdminAuth(request), (request, response) -> sendForbidden(response));
    }
}