package ru.job4j.ee.store.web.auth;

import com.google.inject.Inject;
import ru.job4j.ee.store.model.User;
import ru.job4j.ee.store.service.SecurityService;
import ru.job4j.ee.store.web.ActionDispatcherServlet;
import ru.job4j.ee.store.web.json.JsonUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static ru.job4j.ee.store.util.ServletUtil.forwardToJsp;
import static ru.job4j.ee.store.web.Action.empty;
import static ru.job4j.ee.store.web.auth.AuthUtil.*;
import static ru.job4j.ee.store.web.json.JsonUtil.fromJson;

/**
 * Represents web layer of the app that serves user sign in operations
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-11
 */
public class LoginServlet extends ActionDispatcherServlet {
    @Inject
    private SecurityService service;

    @Override
    protected void fillGetActions() {
        submitGetAction(empty, this::getLogin);
    }

    @Override
    protected void fillPostActions() {
        submitPostAction(empty, this::doLogin);
    }

    private void getLogin(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        var authUser = getAuthUser(request, false);
        if (authUser != null) {
            redirectToMain(request, response);
        } else {
            setUnauthorizedUser(request);
            forwardToJsp("login", request, response);
        }
    }

    private void doLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var credentials = fromJson(request.getReader(), User.class);
        var authUser = service.find(credentials);
        setAuthorizedUser(request, authUser);
        JsonUtil.asJsonToResponse(response, "redirection", redirectTo(request));
    }
}