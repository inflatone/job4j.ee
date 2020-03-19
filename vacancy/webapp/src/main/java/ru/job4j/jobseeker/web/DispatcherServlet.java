package ru.job4j.jobseeker.web;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

import static ru.job4j.jobseeker.web.WebHelper.errorAsJsonToResponse;
import static ru.job4j.jobseeker.web.WebHelper.getRootCause;

/**
 * Represents the template class to implement servlet-handlers to dispatch http GET/POST requests
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2020-01-16
 */
public abstract class DispatcherServlet<T extends Enum<T>> extends HttpServlet {
    private static final String PREFIX = "WEB-INF/jsp/";
    private static final String SUFFIX = ".jsp";
    private static final String REDIRECT_FORMAT = PREFIX + "%s" + SUFFIX;

    private final Class<T> enumClass;
    private final String dispatchParameter;
    private final T emptyAction;
    /**
     * Stores available actions on GET requests executing
     */
    private final Map<T, RequestProcessor> getActions;

    /**
     * Stores available actions on POST requests executing
     */
    private final Map<T, RequestProcessor> postActions;

    public DispatcherServlet(Class<T> enumClass, T emptyAction) {
        this.enumClass = enumClass;
        this.emptyAction = emptyAction;

        dispatchParameter = enumClass.getSimpleName().toLowerCase();
        getActions = new EnumMap<>(enumClass);
        postActions = new EnumMap<>(enumClass);
    }

    /**
     * Must contain the logic of filling of GET requests executors map
     */
    protected void fillGetActions() {
        // empty by default, should be overridden
    }

    /**
     * Must contain the logic of filling of POST requests executors map
     */
    protected void fillPostActions() {
        // empty by default, should be overridden
    }

    /**
     * Adds a new action to the GET requests executors map
     *
     * @param action    action type
     * @param processor action executor bonded to the action
     */
    protected void submitGetAction(T action, RequestProcessor processor) {
        getActions.put(action, processor);
    }

    /**
     * Adds a new action to the POST request executors map
     *
     * @param action    action type
     * @param processor action executor bonded to the action
     */
    protected void submitPostAction(T action, RequestProcessor processor) {
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
        requestProcessorWrapper(getActions, request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        requestProcessorWrapper(postActions, request, response);
    }

    /**
     * Wraps request processor execution and app's exception handling,
     * writes error message from the thrown exception to the response to be shown on the client side
     *
     * @param processors request processors to choose on depend on action from the given request
     * @param request    request
     * @param response   response
     */
    private void requestProcessorWrapper(Map<T, RequestProcessor> processors, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {
            processAction(request, response, processors);
        } catch (RuntimeException e) {
            var message = e.getMessage();
            if (message == null) {
                var root = getRootCause(e);
                message = root.getMessage();
            }
            errorAsJsonToResponse(response, message != null ? message : e.getClass().getSimpleName());
        }
    }

    /**
     * Extracts and defines an action to dispatch from the request parameter map
     *
     * @param request request
     * @return action
     */
    T defineDispatchParam(HttpServletRequest request) {
        T result = WebHelper.getParameter(request, dispatchParameter, s -> T.valueOf(enumClass, s));
        return result == null ? emptyAction : result;
    }

    /**
     * Includes the logic of redirecting the given request to error JSP (in case of wrong action request)
     *
     * @param request  request
     * @param response response
     */
    private void handleError(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Wrong action has been chosen, please try again");
    }

    /**
     * Generalizes the logic of the mapping the given request to relevant action executor and its launch then
     *
     * @param request  request
     * @param response response
     * @param actions  action resolver map
     */
    private void processAction(HttpServletRequest request, HttpServletResponse response, Map<T, RequestProcessor> actions) throws ServletException, IOException {
        var action = WebHelper.suppressException(() -> defineDispatchParam(request), null);
        actions.getOrDefault(action, this::handleError).process(request, response);
    }

    /**
     * Includes the logic of forwarding the given request to JSP path associated with the given JSP name
     *
     * @param jspName  page name
     * @param request  request
     * @param response response
     */
    public static void forwardToJsp(String jspName, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        var url = String.format(REDIRECT_FORMAT, jspName);
        request.getRequestDispatcher(url).forward(request, response);
    }

    /**
     * HttpServletRequest executor (supplier) throwing IO/Servlet exceptions
     */
    @FunctionalInterface
    public interface RequestProcessor {
        void process(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;
    }
}