package ru.job4j.jobseeker.web;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.slf4j.Logger;
import ru.job4j.jobseeker.model.User;
import ru.job4j.jobseeker.service.UserService;
import ru.job4j.jobseeker.web.security.AuthManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.job4j.jobseeker.web.Action.*;
import static ru.job4j.jobseeker.web.WebHelper.*;
import static ru.job4j.jobseeker.web.json.JsonHelper.fromJson;

/**
 * Represents web layer of the app that serves user data requests (from ADMIN role users)
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2020-01-16
 */
public class AdminController extends ActionDispatcherServlet {
    private static final Logger log = getLogger(AdminController.class);

    final Provider<AuthManager> authManagerProvider;

    final UserService service;

    @Inject
    public AdminController(UserService service, Provider<AuthManager> managerProvider) {
        this.service = service;
        this.authManagerProvider = managerProvider;
    }

    @Override
    protected void fillGetActions() {
        submitGetAction(find, this::find);
        submitGetAction(empty, this::getView);
    }

    @Override
    protected void fillPostActions() {
        submitPostAction(save, this::doSave);
        submitPostAction(delete, this::doRemove);
    }

    /**
     * Represents CREATE/UPDATE USER executor
     *
     * @param request  request
     * @param response response
     */
    private void doSave(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var user = createModel(request);
        AuthManager manager = authManagerProvider.get();
        if (user.isNew()) {
            doCreate(user);
            manager.setAuth(user, false);
            response.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            doUpdate(user);
            manager.setAuth(user, Objects.equals(manager.getAuth().getId(), user.getId()));
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        }
    }

    /**
     * Submits the given user model object to service to create user data
     *
     * @param user user model
     */
    private void doCreate(User user) {
        log.info("Create {}", user);
        service.create(user);
    }

    /**
     * Submits the given user model object to service to update user data
     *
     * @param user user model
     */
    private void doUpdate(User user) {
        log.info("Update {}", user);
        service.update(user);
    }

    /**
     * Represents DELETE USER executor
     *
     * @param request  request
     * @param response response
     */
    private void doRemove(HttpServletRequest request, HttpServletResponse response) {
        int id = getRequiredId(request);
        log.info("Delete {}", id);
        service.delete(id);
        authManagerProvider.get().removeAuth(id);
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    /**
     * Writes to the response user data (in dependence of request parameters) as JSON object
     *
     * @param request  request
     * @param response response
     */
    void find(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var id = getParameter(request, "id", Integer::parseInt);
        if (id == null) {
            log.info("find all users");
            var users = service.findAll();
            asJsonToResponse(response, users);
        } else {
            log.info("find user with id={}", id);
            var user = service.find(id);
            asJsonToResponse(response, user);
        }
    }

    /**
     * Represents USER LIST page redirector
     *
     * @param request  request
     * @param response response
     */
    void getView(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        forwardToJsp("users", request, response);
    }

    /**
     * Extracts the id parameter from the given request, transforms it to {@link int} value,
     * throws NPE if it's not found
     *
     * @param request request
     * @return user id
     */
    int getRequiredId(HttpServletRequest request) {
        return getRequiredParameter(request, "id", Integer::parseInt);
    }

    /**
     * Wraps the given data from the servlet request in a transfer object
     *
     * @param request request
     * @return user transfer object
     */
    User createModel(HttpServletRequest request) throws IOException {
        return fromJson(request.getReader(), User.class);
    }
}