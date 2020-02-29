package ru.job4j.ee.store.web;

import org.slf4j.Logger;
import ru.job4j.ee.store.model.User;
import ru.job4j.ee.store.service.UserService;
import ru.job4j.ee.store.web.dispatchers.RequestDispatcher;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.job4j.ee.store.service.UserService.getUserService;
import static ru.job4j.ee.store.web.dispatchers.UserRequestDispatcher.getDispatcher;

/**
 * Represents web layer of the app that serves user data requests
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-05
 */
public class UserServlet extends HttpServlet {
    private static final Logger log = getLogger(UserServlet.class);

    private final UserService service = getUserService();

    private final RequestDispatcher<User> dispatcher = getDispatcher();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        dispatcher.init();
    }

    /**
     * Contains the only action yet: find all users in the store and print them to the response object
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        log.info("GetAll");
        dispatcher.doResponse(response, service.findAll());
    }

    /**
     * Dispatches the incoming requests depending on which action parameter the request contains
     * https://github.com/peterarsentev/code_quality_principles#2-dispatch-pattern-instead-of-multiple-if-statements-and-switch-anti-pattern
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        dispatcher.process(request);
        response.sendRedirect(request.getContextPath() + "/list");
    }
}