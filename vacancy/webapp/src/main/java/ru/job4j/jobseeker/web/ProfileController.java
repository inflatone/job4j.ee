package ru.job4j.jobseeker.web;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.job4j.jobseeker.model.User;
import ru.job4j.jobseeker.service.UserService;
import ru.job4j.jobseeker.web.security.AuthManager;

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
    @Inject
    public ProfileController(UserService service, Provider<AuthManager> managerProvider) {
        super(service, managerProvider);
    }

    @Override
    void find(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var id = getRequiredId(request);
        asJsonToResponse(response, service.find(id));
    }

    @Override
    User createModel(HttpServletRequest request) throws IOException {
        var user = super.createModel(request);
        // users can 'crud' only themselves
        AuthManager manager = authManagerProvider.get();
        user.setId(manager == null ? null : manager.findAllowedId(user::getId));
        return user;
    }

    @Override
    int getRequiredId(HttpServletRequest request) {
        return authManagerProvider.get().getRequiredUserId(request);
    }

    @Override
    void getView(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        forwardToJsp("profile", request, response);
    }
}