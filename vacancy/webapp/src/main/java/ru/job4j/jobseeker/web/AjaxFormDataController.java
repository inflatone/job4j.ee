package ru.job4j.jobseeker.web;

import com.google.inject.Inject;
import one.util.streamex.StreamEx;
import org.slf4j.Logger;
import ru.job4j.jobseeker.model.Role;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.job4j.jobseeker.web.AjaxFormDataController.Form.user;
import static ru.job4j.jobseeker.web.WebHelper.asJsonToResponse;

/**
 * Represents web layer of the app that serves additional data requests
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2020-01-16
 */
public class AjaxFormDataController extends DispatcherServlet<AjaxFormDataController.Form> {
    private static final Logger log = getLogger(AjaxFormDataController.class);

    @Inject
    public AjaxFormDataController() {
        super(Form.class, null);
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
                "roles", StreamEx.of(EnumSet.allOf(Role.class)).toMap(Role::ordinal, Enum::name));
        asJsonToResponse(response, data);
    }

    enum Form {
        user
    }
}