package ru.job4j.ee.store.web.auth;

import ru.job4j.ee.store.model.User;
import ru.job4j.ee.store.service.SecurityService;
import ru.job4j.ee.store.web.DispatcherServlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static ru.job4j.ee.store.service.SecurityService.getSecurityService;
import static ru.job4j.ee.store.web.Action.empty;
import static ru.job4j.ee.store.web.auth.AuthUtil.*;

/**
 * Represents web layer of the app that serves user sign in operations
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-11
 */
public class LoginServlet extends DispatcherServlet {
    private final SecurityService service = getSecurityService();

    @Override
    protected void fillGetActions() {
        submitGetAction(empty, this::getLogin);
    }

    @Override
    protected void fillPostActions() {
        submitPostAction(empty, this::doLogin);
    }

    @Override
    public void sendRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        redirectToMain(request, response);
    }

    private String getLogin(HttpServletRequest request) {
        setUnauthorizedUser(request);
        return "login";
    }

    private void doLogin(HttpServletRequest request) {
        var credentials = createCredentialsModel(request);
        var authUser = service.find(credentials);
        setAuthorizedUser(request, authUser);
    }

    private User createCredentialsModel(HttpServletRequest request) {
        return new User(null, null,
                request.getParameter("login"), request.getParameter("password"),
                null, null, true, null);
    }
}