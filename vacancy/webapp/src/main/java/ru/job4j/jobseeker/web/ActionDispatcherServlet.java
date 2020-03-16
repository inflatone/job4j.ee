package ru.job4j.jobseeker.web;

/**
 * Represents the template class to implement action-parameter servlet dispatching
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2020-01-16
 */
public abstract class ActionDispatcherServlet extends DispatcherServlet<Action> {
    public ActionDispatcherServlet() {
        super(Action.class, Action.empty);
    }
}