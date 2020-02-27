package ru.job4j.ee.store.web.dispatchers;

import ru.job4j.ee.store.web.Action;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

import static ru.job4j.ee.store.web.Action.defineAction;

/**
 * Represents a template to implement specific entity dispatchers of requests with the given actions
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-05
 */
public abstract class RequestDispatcher<T> {
    /**
     * Stores available actions to execute
     */
    private final Map<Action, RequestProcessor> actions = new EnumMap<>(Action.class);

    /**
     * Contains init logic of dispatcher objects
     */
    public abstract void init();

    /**
     * Contains logic of writing to response entity model elements
     *
     * @param response response
     * @param model    iterable entities
     */
    public abstract void doResponse(HttpServletResponse response, Iterable<T> model) throws IOException;

    /**
     * Adds an action to the dispatcher
     *
     * @param type   action type
     * @param action action executor bonded to the action
     */
    public void load(Action type, RequestProcessor action) {
        actions.put(type, action);
    }

    /**
     * Dispatches an action received from the request
     *
     * @param request request
     */
    public void process(final HttpServletRequest request) throws IOException, ServletException {
        actions.get(consumeAction(request)).process(request);
    }

    /**
     * Gets and defines an action to dispatch from the request parameter map
     *
     * @param request request
     */
    Action consumeAction(final HttpServletRequest request) {
        String action = request.getParameter("action");
        return defineAction(action);
    }

    /**
     * HttpServletRequest executor (supplier) throwing IO/Servlet exceptions
     */
    @FunctionalInterface
    public interface RequestProcessor {
        void process(HttpServletRequest request) throws IOException, ServletException;
    }
}