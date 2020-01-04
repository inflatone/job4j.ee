package ru.job4j.ee.store.web;

import ru.job4j.ee.store.model.Role;
import ru.job4j.ee.store.model.User;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static ru.job4j.ee.store.util.ServletUtil.forwardToJsp;
import static ru.job4j.ee.store.web.auth.AuthUtil.getAuthUser;
import static ru.job4j.ee.store.web.auth.AuthUtil.retrieveIfAdminOrCheckSession;
import static ru.job4j.ee.store.web.json.JsonUtil.asJsonToResponse;

/**
 * Represents web layer of the app that serves user data requests (from NO ADMIN role users)
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-12
 */
public class ProfileServlet extends AdminServlet {
    @Override
    void find(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var id = getRequiredId(request);
        asJsonToResponse(response, service.find(id));
    }

    @Override
    User createModel(HttpServletRequest request) throws IOException {
        var user = super.createModel(request);
        var auth = getAuthUser(request, false);
        if (auth == null) {
            user.setId(null);
        } else if (auth.getRole() != Role.ADMIN) {
            user.setId(auth.getId()); // users can 'crud' only themselves
        }
        return user;
    }

    @Override
    int getRequiredId(HttpServletRequest request) {
        Integer userId = retrieveIfAdminOrCheckSession(request, "id", Integer::parseInt);
        return userId != null ? userId : getAuthUser(request, true).getId(); // users can 'crud' only themselves
    }

    @Override
    void getView(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        forwardToJsp("profile", request, response);
    }
}