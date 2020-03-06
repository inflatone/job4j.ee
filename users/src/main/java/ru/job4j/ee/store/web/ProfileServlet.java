package ru.job4j.ee.store.web;

import ru.job4j.ee.store.model.Role;
import ru.job4j.ee.store.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static ru.job4j.ee.store.web.auth.AuthUtil.getAuthUser;

/**
 * Represents web layer of the app that serves user data requests (from NO ADMIN role users)
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-12
 */
public class ProfileServlet extends AdminServlet {
    @Override
    Role extractRole(HttpServletRequest request) {
        User authUser = getAuthUser(request, false);
        return authUser == null ? Role.USER : authUser.getRole();
    }

    @Override
    int getRequiredId(HttpServletRequest request) { // users can 'crud' only themselves
        return getAuthUser(request, true).getId();
    }

    @Override
    protected void sendRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getContextPath() + "/profile");
    }

    @Override
    String showView(HttpServletRequest request) {
        request.setAttribute("user", service.find(getAuthUser(request, true).getId()));
        return "profile";
    }
}