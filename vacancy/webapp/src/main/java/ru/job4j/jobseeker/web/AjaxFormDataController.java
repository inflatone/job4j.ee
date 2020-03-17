package ru.job4j.jobseeker.web;

import com.google.inject.Inject;
import com.google.inject.Provider;
import one.util.streamex.StreamEx;
import org.slf4j.Logger;
import ru.job4j.jobseeker.model.Role;
import ru.job4j.jobseeker.web.security.AuthManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.job4j.jobseeker.web.AjaxFormDataController.Form;
import static ru.job4j.jobseeker.web.AjaxFormDataController.Form.user;
import static ru.job4j.jobseeker.web.WebHelper.asJsonToResponse;

/**
 * Represents web layer of the app that serves additional data requests
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2020-01-16
 */
public class AjaxFormDataController extends DispatcherServlet<Form> {
    private static final Logger log = getLogger(AjaxFormDataController.class);

    private final Provider<AuthManager> authManagerProvider;

    @Inject
    public AjaxFormDataController(Provider<AuthManager> managerProvider) {
        super(Form.class, null);
        this.authManagerProvider = managerProvider;
    }

    @Override
    protected void fillGetActions() {
        submitGetAction(user, this::roles);
    }

    /**
     * Writes to the response the set of available roles as JSON object
     *
     * @param request  request
     * @param response response
     */
    private void roles(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("Send roles to user form");
        var data = Map.of(
                "roles", StreamEx.of(composeAvailableRoles()).toMap(Role::ordinal, Enum::name));
        asJsonToResponse(response, data);
    }

    /**
     * Composes the set of available roles (in dependence of authorized user role)
     *
     * @return available roles
     */
    private Set<Role> composeAvailableRoles() {
        return authManagerProvider.get().isAuthorizedAsAdmin() ? EnumSet.allOf(Role.class) : Set.of(Role.USER);
    }

    enum Form {
        user
    }
}