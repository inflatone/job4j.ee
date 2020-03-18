package ru.job4j.jobseeker.web.security;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.job4j.jobseeker.service.SecurityService;
import ru.job4j.jobseeker.web.ActionDispatcherServlet;
import ru.job4j.jobseeker.web.WebHelper;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static ru.job4j.jobseeker.web.Action.empty;
import static ru.job4j.jobseeker.web.WebHelper.getRequiredParameter;

/**
 * Serves user sign-in operations
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2020-01-16
 */
public class LoginController extends ActionDispatcherServlet {
    private final SecurityService service;
    private final Provider<AuthManager> authManagerProvider;

    @Override
    protected void fillGetActions() {
        submitGetAction(empty, this::login);
    }

    @Override
    protected void fillPostActions() {
        submitPostAction(empty, this::doLogin);
    }

    @Inject
    public LoginController(SecurityService service, Provider<AuthManager> authManagerProvider) {
        this.service = service;
        this.authManagerProvider = authManagerProvider;
    }

    private void login(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        if (authManagerProvider.get().isAuthorized()) {
            response.sendRedirect(request.getContextPath() + "/profile");
        } else {
            forwardToJsp("login", request, response);
        }
    }

    private void doLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var login = getRequiredParameter(request, "login");
        var password = getRequiredParameter(request, "password");
        var auth = service.find(login, password);

        authManagerProvider.get().setAuth(auth, true);
        WebHelper.asJsonToResponse(response, "redirection", "profile");
    }
}
