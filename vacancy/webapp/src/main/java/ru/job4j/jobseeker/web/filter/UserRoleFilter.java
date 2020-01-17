package ru.job4j.jobseeker.web.filter;

import com.google.inject.Inject;
import com.google.inject.Provider;
import ru.job4j.jobseeker.web.security.AuthManager;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Put the auth user model to request scope
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2020-01-17
 */
public class UserRoleFilter extends HttpFilter {
    private final Provider<AuthManager> authManagerProvider;

    @Inject
    public UserRoleFilter(Provider<AuthManager> managerProvider) {
        this.authManagerProvider = managerProvider;
    }


    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        var auth = authManagerProvider.get().getAuth();
        if (auth != null) {
            request.setAttribute("role", auth.getRole());
        }
        chain.doFilter(request, response);
    }
}
