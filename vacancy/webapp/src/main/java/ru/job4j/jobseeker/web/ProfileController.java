package ru.job4j.jobseeker.web;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static ru.job4j.jobseeker.web.WebHelper.asJsonToResponse;

/**
 * Represents web layer of the app that serves user data requests (from NO ADMIN role users)
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2020-01-16
 */
public class ProfileController extends AdminController {

    @Override
    void find(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var id = getRequiredId(request);
        asJsonToResponse(response, USERS.get(id));
    }

    @Override
    void getView(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        forwardToJsp("profile", request, response);
    }
}