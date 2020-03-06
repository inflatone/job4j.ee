package ru.job4j.ee.store.web.filters;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Represents a abstract servlet requests' filter that checks and redirects the deniable requests according on the given the condition of denying
 * and handle function
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-11
 */
public abstract class AbstractRequestFilter extends HttpFilter {
    private final FilterCondition filterCondition;

    private final FilterSolution filterSolution;

    AbstractRequestFilter(FilterCondition filterCondition, FilterSolution filterSolution) {
        this.filterCondition = filterCondition;
        this.filterSolution = filterSolution;
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (filterCondition.isForbidden(request)) {
            filterSolution.handle(request, response);
        } else {
            chain.doFilter(request, response);
        }
    }

    @FunctionalInterface
    public interface FilterCondition {
        boolean isForbidden(HttpServletRequest request) throws IOException, ServletException;
    }

    @FunctionalInterface
    public interface FilterSolution {
        void handle(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException;
    }
}
