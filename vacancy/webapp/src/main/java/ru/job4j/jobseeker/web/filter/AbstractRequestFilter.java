package ru.job4j.jobseeker.web.filter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Represents an template of servlet request interrupter that checks the given request state
 * and performs the interruption action on the request if its state defines as incorrect or forbidden
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2020-01-16
 */
public abstract class AbstractRequestFilter extends HttpFilter {

    /**
     * Offer the validator of request state condition which means that the request processing should be pre-extended
     *
     * @param request request
     * @return true if request should be declined
     */
    abstract boolean needAct(HttpServletRequest request) throws IOException, ServletException;

    /**
     * Defines the action that should be applied in case of request state passes needAct() validation
     *
     * @param request  request
     * @param response response
     */
    abstract void act(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (needAct(request)) {
            act(request, response);
        } else {
            chain.doFilter(request, response);
        }
    }
}