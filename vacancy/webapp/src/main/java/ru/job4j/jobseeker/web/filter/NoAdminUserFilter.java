package ru.job4j.jobseeker.web.filter;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.job4j.jobseeker.web.security.AuthManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Represents a servlet filter that admits/denies user data requests to the admin part of the app
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2020-01-16
 */
public class NoAdminUserFilter extends AbstractRequestFilter {
    private final Provider<AuthManager> authManagerProvider;

    @Inject
    public NoAdminUserFilter(Provider<AuthManager> managerProvider) {
        this.authManagerProvider = managerProvider;
    }


    @Override
    boolean needAct(HttpServletRequest request) {
        return !authManagerProvider.get().isAuthorizedAsAdmin();
    }

    @Override
    void act(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "You're not authorized to pass here");
    }
}
