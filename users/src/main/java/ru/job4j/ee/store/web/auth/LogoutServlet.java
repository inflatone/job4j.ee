package ru.job4j.ee.store.web.auth;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static ru.job4j.ee.store.web.auth.AuthUtil.setUnauthorizedUser;

/**
 * Represents web layer of the app that serves user sign out operations
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-11
 */
public class LogoutServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        setUnauthorizedUser(request);
        response.sendRedirect(request.getContextPath() + '/');
    }
}