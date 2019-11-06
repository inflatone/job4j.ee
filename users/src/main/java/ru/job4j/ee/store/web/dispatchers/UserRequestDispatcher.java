package ru.job4j.ee.store.web.dispatchers;

import one.util.streamex.StreamEx;
import org.slf4j.Logger;
import ru.job4j.ee.store.model.User;
import ru.job4j.ee.store.service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.job4j.ee.store.service.UserService.getUserService;
import static ru.job4j.ee.store.util.ServletUtil.getRequiredId;
import static ru.job4j.ee.store.web.Action.*;

/**
 * Represents logic of dispatching user entity requests
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-05
 */
public class UserRequestDispatcher extends RequestDispatcher<User> {
    private static final Logger log = getLogger(UserRequestDispatcher.class);
    private static final RequestDispatcher<User> INSTANCE_HOLDER = new UserRequestDispatcher();

    public static RequestDispatcher<User> getDispatcher() {
        return INSTANCE_HOLDER;
    }

    private final UserService service = getUserService();

    @Override
    public void init() {
        load(create, this::create);
        load(update, this::update);
        load(delete, this::delete);
    }

    @Override
    public void doResponse(HttpServletResponse response, Iterable<User> users) throws IOException {
        String out = StreamEx.of(users).joining("\n");
        response.getWriter().println(out);
    }

    /**
     * Represents CREATE USER executor
     *
     * @param request request
     */
    void create(HttpServletRequest request) {
        User user = composeModel(null, request);
        log.info("Create {}", user);
        service.create(user);
    }

    /**
     * Represents UPDATE USER executor
     *
     * @param request request
     */
    void update(HttpServletRequest request) {
        User user = composeModel(getRequiredId(request), request);
        log.info("Update {}", user);
        service.update(user);
    }

    /**
     * Represents DELETE USER executor
     *
     * @param request request
     */
    void delete(HttpServletRequest request) {
        int id = getRequiredId(request);
        log.info("Delete {}", id);
        service.delete(id);
    }

    /**
     * Wraps the given data from the servlet request in a transfer object
     *
     * @param id      user id
     * @param request request
     * @return user transfer object
     */
    private User composeModel(Integer id, HttpServletRequest request) {
        return new User(id, request.getParameter("name"),
                request.getParameter("login"), request.getParameter("password"), new Date());
    }
}
