package ru.job4j.ee.store.web;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Represents the template class to implement servlet-handlers to dispatch http GET/POST requests
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-07
 */
public abstract class DispatcherServlet extends HttpServlet {
    private static final String PREFIX = "WEB-INF/jsp/";
    private static final String SUFFIX = ".jsp";
    private static final String REDIRECT_FORMAT = PREFIX + "%s" + SUFFIX;

    /**
     * Stores available actions on GET requests executing
     */
    private final Map<Action, RequestRedirector> getActions = new EnumMap<>(Action.class);

    /**
     * Stores available actions on POST requests executing
     */
    private final Map<Action, RequestProcessor> postActions = new EnumMap<>(Action.class);

    /**
     * Must contain the logic of filling of GET requests executors map
     */
    abstract void fillGetActions();

    /**
     * Must contain the logic of filling of POST requests executors map
     */
    abstract void fillPostActions();

    /**
     * Must contain the logic of redirecting to certain page after POST requests completing
     *
     * @param request  request
     * @param response response
     */
    abstract void sendRedirect(HttpServletRequest request, HttpServletResponse response) throws IOException;

    /**
     * Adds a new action to the GET requests executors map
     *
     * @param action     action type
     * @param redirector action executor (redirector to JSP) bonded to the action
     */
    void submitGetAction(Action action, RequestRedirector redirector) {
        getActions.put(action, redirector);
    }

    /**
     * Adds a new action to the POST request executors map
     *
     * @param action    action type
     * @param processor action executor (form processor) bonded to the action
     */
    void submitPostAction(Action action, RequestProcessor processor) {
        postActions.put(action, processor);
    }

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        fillGetActions();
        fillPostActions();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setCharacterEncoding(UTF_8.name());
        var action = defineAction(request);
        var url = getActions.getOrDefault(action, this::getError).path(request);
        request.getRequestDispatcher(url).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        var action = defineAction(request);
        var processor = postActions.get(action);
        if (processor == null) {
            postError(request, response);
        } else {
            processor.process(request);
            sendRedirect(request, response);
        }
    }

    /**
     * Extracts and defines an action to dispatch from the request parameter map
     *
     * @param request request
     * @return action
     */
    Action defineAction(HttpServletRequest request) {
        var action = request.getParameter("action");
        return Action.defineAction(action);
    }

    /**
     * Sets the given page name to the redirect formatter ("WEB-INF/jsp/*.jsp")
     *
     * @param jspName page name
     * @return JSP path to redirect
     */
    String createRedirection(String jspName) {
        return String.format(REDIRECT_FORMAT, jspName);
    }

    /**
     * Composes the error page path to redirect in case of app error
     * May contain the logic of putting into ctx some error object to access from JSP in future
     *
     * @param request can be used to extract additional info to compose the message showing on error page
     * @return error JSP path to redirect in case of app error
     */
    String getError(HttpServletRequest request) {
        return createRedirection("error");
    }

    /**
     * Includes the logic of redirecting the given request to error JSP (in case of wrong action request)
     *
     * @param request  request
     * @param response response
     */
    void postError(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        var url = getError(request);
        request.getRequestDispatcher(url).forward(request, response);
    }

    /**
     * HttpServletRequest redirector (function mapping requests to JSP paths)
     */
    @FunctionalInterface
    public interface RequestRedirector {
        String path(HttpServletRequest request);
    }

    /**
     * HttpServletRequest executor (supplier) throwing IO/Servlet exceptions
     */
    @FunctionalInterface
    public interface RequestProcessor {
        void process(HttpServletRequest request) throws IOException, ServletException;
    }
}