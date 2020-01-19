package ru.job4j.jobseeker.web;

import com.google.inject.Inject;
import com.google.inject.Provider;
import one.util.streamex.StreamEx;
import org.slf4j.Logger;
import ru.job4j.jobseeker.model.BaseEntity;
import ru.job4j.jobseeker.model.RepeatRule;
import ru.job4j.jobseeker.model.Role;
import ru.job4j.jobseeker.model.ScanSource;
import ru.job4j.jobseeker.service.TaskService;
import ru.job4j.jobseeker.web.security.AuthManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Map;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.job4j.jobseeker.web.AjaxFormDataController.Form;
import static ru.job4j.jobseeker.web.AjaxFormDataController.Form.task;
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

    private final TaskService service;
    private final Provider<AuthManager> authManagerProvider;

    @Inject
    public AjaxFormDataController(TaskService service, Provider<AuthManager> managerProvider) {
        super(Form.class, null);
        this.service = service;
        this.authManagerProvider = managerProvider;
    }

    @Override
    protected void fillGetActions() {
        submitGetAction(user, this::roles);
        submitGetAction(task, this::tasks);
    }

    /**
     * Writes to the response the set of available roles as JSON object
     *
     * @param request  request
     * @param response response
     */
    private void roles(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("Send roles to user form");
        var data = Map.of("roles", composeAvailableRoles());
        asJsonToResponse(response, data);
    }

    /**
     * Composes the set of available roles (in dependence of authorized user role)
     *
     * @return available roles
     */
    private Map<Integer, String> composeAvailableRoles() {
        return authManagerProvider.get().isAuthorizedAsAdmin() ? allAsMap(Role.class) : asMap(Role.USER);
    }

    /**
     * Writes to the response the all available rules and all available providers as JSON object
     * to be pasted as form select options
     *
     * @param request  request
     * @param response response
     */
    private void tasks(HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.info("Send cron rules + vacancy providers to task form");
        var data = Map.of(
                "rules", allAsMap(RepeatRule.class),
                "sources", StreamEx.of(service.findAllSources()).toMap(BaseEntity::getId, ScanSource::getTitle));
        asJsonToResponse(response, data);
    }

    private static <E extends Enum<E>> Map<Integer, String> asMap(E e) {
        return StreamEx.of(EnumSet.of(e)).toMap(Enum::ordinal, Enum::name);
    }

    private static <E extends Enum<E>> Map<Integer, String> allAsMap(Class<E> enumClass) {
        return StreamEx.of(EnumSet.allOf(enumClass)).toMap(Enum::ordinal, Enum::name);
    }

    enum Form {
        user,
        task
    }
}