package ru.job4j.jobseeker.web.security;

import com.google.inject.Inject;
import com.google.inject.Provider;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Serves user sign-out operations
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2020-01-16
 */
public class LogoutController extends HttpServlet {
    private final Provider<AuthManager> managerProvider;

    @Inject
    public LogoutController(Provider<AuthManager> managerProvider) {
        this.managerProvider = managerProvider;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        managerProvider.get().removeAuth();
        response.sendRedirect(request.getContextPath() + '/');
    }
}
