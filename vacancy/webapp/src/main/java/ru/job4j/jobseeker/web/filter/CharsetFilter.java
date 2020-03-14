package ru.job4j.jobseeker.web.filter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Represents a servlet filter that sets standart charset (for correct work with non-latin symbols)
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-11-13
 */
public class CharsetFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        request.setCharacterEncoding(UTF_8.name());
        response.setCharacterEncoding(UTF_8.name());
        chain.doFilter(request, response);
    }
}