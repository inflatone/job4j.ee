package ru.job4j.ee.store.web;

import com.google.inject.Inject;
import org.slf4j.Logger;
import ru.job4j.ee.store.model.User;
import ru.job4j.ee.store.service.CityService;
import ru.job4j.ee.store.service.UserService;
import ru.job4j.ee.store.util.ServletUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.job4j.ee.store.util.ServletUtil.*;
import static ru.job4j.ee.store.web.Action.*;
import static ru.job4j.ee.store.web.auth.AuthUtil.setAuthorizedIfUnauthorized;
import static ru.job4j.ee.store.web.auth.AuthUtil.setUnauthorizedIfSameId;
import static ru.job4j.ee.store.web.json.JsonUtil.asJsonToResponse;
import static ru.job4j.ee.store.web.json.JsonUtil.fromJson;

/**
 * Represents web layer of the app that serves user data requests (from ADMIN role users)
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-12
 */
public class AdminServlet extends ActionDispatcherServlet {
    private static final Logger log = getLogger(AdminServlet.class);

    @Inject
    UserService service;

    @Inject
    private CityService cityService;

    @Override
    protected void fillGetActions() {
        submitGetAction(find, this::find);
        submitGetAction(empty, this::getView);
    }

    @Override
    protected void fillPostActions() {
        submitPostAction(save, this::doSave);
        submitPostAction(delete, this::doRemove);
        submitPostAction(enable, this::doEnable);
    }

    /**
     * Represents CREATE/UPDATE USER executor
     *
     * @param request  request
     * @param response response
     */
    private void doSave(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var user = createModel(request);
        if (user.isNew()) {
            doCreate(user);
            setAuthorizedIfUnauthorized(request, user);
            response.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            doUpdate(user);
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
     * Represents ENABLE/DISABLE USER executor
     *
     * @param request  request
     * @param response response
     */
    private void doEnable(HttpServletRequest request, HttpServletResponse response) {
        int id = getRequiredId(request);
        boolean enabled = getRequiredParameter(request, "enabled", Boolean::valueOf);
        log.info(enabled ? "Enable {}" : "Disable {}", id);
        service.enable(id, enabled);
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
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
        setUnauthorizedIfSameId(request, id);
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    /**
     * Writes to the response user data (in dependence of request parameters) as JSON object
     *
     * @param request  request
     * @param response response
     */
    void find(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var id = getId(request, false);
        if (id == null) {
            log.info("getALL users");
            var users = service.findAll();
            asJsonToResponse(response, users);
        } else {
            log.info("get users with id={}", id);
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
        forwardToJsp("list", request, response);
    }

    /**
     * Extracts the id parameter from the given request, transforms it to {@link int} value,
     * throws NPE if it's not found
     *
     * @param request request
     * @return user id
     */
    int getRequiredId(HttpServletRequest request) {
        return ServletUtil.getRequiredId(request);
    }

    /**
     * Wraps the given data from the servlet request in a transfer object
     *
     * @param request request
     * @return user transfer object
     */
    User createModel(HttpServletRequest request) throws IOException {
        return fromJson(request.getReader(), User.class); // attach image is not supported  here anymore
    }
}