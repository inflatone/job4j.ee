package ru.job4j.ee.store.web;

import org.slf4j.Logger;
import ru.job4j.ee.store.model.User;
import ru.job4j.ee.store.model.UserImage;
import ru.job4j.ee.store.service.UserService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.job4j.ee.store.service.UserService.getUserService;
import static ru.job4j.ee.store.util.ServletUtil.getParameter;
import static ru.job4j.ee.store.util.ServletUtil.getRequiredId;
import static ru.job4j.ee.store.web.Action.*;

/**
 * Represents web layer of the app that serves user data requests
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-05
 */
public class UserServlet extends DispatcherServlet {
    private static final Logger log = getLogger(UserServlet.class);

    private final UserService service = getUserService();

    @Override
    void fillGetActions() {
        submitGetAction(create, this::getCreate);
        submitGetAction(update, this::getUpdate);
        submitGetAction(empty, this::getAll);
    }

    @Override
    void fillPostActions() {
        submitPostAction(create, this::doCreate);
        submitPostAction(update, this::doUpdate);
        submitPostAction(delete, this::doDelete);
    }

    @Override
    void sendRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getContextPath() + "/users");
    }

    /**
     * Represents CREATE USER executor
     *
     * @param request request
     */
    void doCreate(HttpServletRequest request) throws IOException, ServletException {
        User user = createModel(null, request);
        log.info("Create {}", user);
        service.create(user);
    }

    /**
     * Represents UPDATE USER executor
     *
     * @param request request
     */
    void doUpdate(HttpServletRequest request) throws IOException, ServletException {
        User user = createModel(getRequiredId(request), request);
        log.info("Update {}", user);
        service.update(user);
    }

    /**
     * Represents DELETE USER executor
     *
     * @param request request
     */
    void doDelete(HttpServletRequest request) {
        int id = getRequiredId(request);
        log.info("Delete {}", id);
        service.delete(id);
    }

    /**
     * Represents CREATE USER page redirector
     *
     * @param request request
     * @return user form path to redirect
     */
    String getCreate(HttpServletRequest request) {
        return createRedirectToForm(request, true);
    }

    /**
     * Represents UPDATE USER page redirector
     *
     * @param request request
     * @return user form path to redirect
     */
    String getUpdate(HttpServletRequest request) {
        return createRedirectToForm(request, false);
    }

    /**
     * Represents USER LIST page redirector
     *
     * @param request request
     * @return user list path to redirect
     */
    String getAll(HttpServletRequest request) {
        request.setAttribute("users", service.findAll());
        return createRedirection("list");
    }

    /**
     * Wraps the given data from the servlet request in a transfer object
     *
     * @param id      user id
     * @param request request
     * @return user transfer object
     */
    private User createModel(Integer id, HttpServletRequest request) throws IOException, ServletException {
        String name = request.getParameter("name");
        String login = request.getParameter("login");
        String password = request.getParameter("password");
        return new User(id, name, login, password, new Date(), extractUserImage(request));
    }

    /**
     * Retrieves image data from the given request
     *
     * @param request request
     * @return image data
     */
    private UserImage extractUserImage(HttpServletRequest request) throws IOException, ServletException {
        var part = request.getPart("image");
        if (part.getSize() == 0) {
            Integer imageId = getParameter(request, "imageId", false, Integer::valueOf);
            return new UserImage(imageId == null ? 0 : imageId); // stub id to prevent mistaken image saving (service call isNew() to detect if it needs to save img)
        }
        return new UserImage(part.getSubmittedFileName(), part.getContentType(), part.getSize(), part.getInputStream());
    }

    /**
     * Creates new user model object or retrieves already existing one (if specified),
     * then puts it into context and returns created JSP form path to redirect the request
     *
     * @param request request request
     * @param isNew   signal if a new model object needed (otherwise it have to be requested to the service
     * @return JSP form path to redirect
     */
    private String createRedirectToForm(HttpServletRequest request, boolean isNew) {
        var userTo = isNew ? buildEmptyModel() : service.find(getRequiredId(request));
        request.setAttribute("user", userTo);
        return createRedirection("form");
    }

    /**
     * Creates an empty user model to paste in form JSP template when 'get create' action chosen
     *
     * @return empty user model
     */
    private User buildEmptyModel() {
        return new User(null, "", "", "", null, null);
    }
}