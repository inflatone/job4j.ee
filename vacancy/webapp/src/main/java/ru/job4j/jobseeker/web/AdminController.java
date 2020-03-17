package ru.job4j.jobseeker.web;

import com.google.inject.Inject;
import com.google.inject.Provider;
import org.slf4j.Logger;
import ru.job4j.jobseeker.model.Role;
import ru.job4j.jobseeker.model.User;
import ru.job4j.jobseeker.web.security.AuthManager;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

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

    public static final Map<Integer, User> USERS = new ConcurrentHashMap<>();

    public static final AtomicInteger SEQ = new AtomicInteger(100000);

    static {
        USERS.put(SEQ.incrementAndGet(), new User(SEQ.get(), "user", "password", new Date(), Role.USER));
        USERS.put(SEQ.incrementAndGet(), new User(SEQ.get(), "admin", "admin", new Date(), Role.ADMIN));
        USERS.put(SEQ.incrementAndGet(), new User(SEQ.get(), "test", "password", new Date(), Role.USER));
        USERS.put(SEQ.incrementAndGet(), new User(SEQ.get(), "simple", "simple", new Date(), Role.USER));
        USERS.put(SEQ.incrementAndGet(), new User(SEQ.get(), "supervisor", "bingo", new Date(), Role.ADMIN));
    }

    final Provider<AuthManager> authManagerProvider;

    @Inject
    public AdminController(Provider<AuthManager> managerProvider) {
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
        //service.create(user);
        user.setId(SEQ.incrementAndGet());
        user.setRegistered(new Date());
        USERS.put(user.getId(), user);
    }

    /**
     * Submits the given user model object to service to update user data
     *
     * @param user user model
     */
    private void doUpdate(User user) {
        log.info("Update {}", user);

        USERS.computeIfPresent(user.getId(), (k, v) -> {
            v.setLogin(user.getLogin());
            v.setPassword(user.getPassword());
            v.setRole(user.getRole());
            return v;
        });

        //service.update(user);
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

        USERS.remove(id);
        //service.delete(id);

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
            log.info("getALL users");
            var users = USERS.values();
            asJsonToResponse(response, users);
        } else {
            log.info("get users with id={}", id);
            var user = USERS.get(id);
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