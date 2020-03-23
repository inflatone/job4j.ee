package ru.job4j.jobseeker.web;

import com.google.inject.Provider;
import org.slf4j.Logger;
import ru.job4j.jobseeker.service.VacancyService;
import ru.job4j.jobseeker.web.security.AuthManager;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static java.util.Objects.requireNonNull;
import static org.slf4j.LoggerFactory.getLogger;
import static ru.job4j.jobseeker.web.Action.*;
import static ru.job4j.jobseeker.web.WebHelper.asJsonToResponse;
import static ru.job4j.jobseeker.web.WebHelper.getRequiredParameter;

/**
 * Represents web layer of the app that serves vacancy data requests
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2020-01-22
 */
public class VacancyController extends ActionDispatcherServlet {
    private static final Logger log = getLogger(VacancyController.class);

    private final VacancyService service;
    private final Provider<AuthManager> authManagerProvider;

    @Inject
    public VacancyController(VacancyService service, Provider<AuthManager> authManagerProvider) {
        this.service = service;
        this.authManagerProvider = authManagerProvider;
    }

    @Override
    protected void fillGetActions() {
        submitGetAction(find, this::find);
    }

    @Override
    protected void fillPostActions() {
        submitPostAction(empty, this::doHighlight);
        submitPostAction(delete, this::doRemove);
    }

    private void find(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int userId = getRequiredUserId(request);
        int taskId = getRequiredParameter(request, "taskId", Integer::parseInt);
        log.info("Find all vacancies from task with id={} for user with id={}", taskId, userId);
        var vacancies = service.findAll(taskId, userId);
        asJsonToResponse(response, vacancies);
    }

    /**
     * Represents SET VACANCY HIGHLIGHT executor
     *
     * @param request  request
     * @param response response
     */
    private void doHighlight(HttpServletRequest request, HttpServletResponse response) {
        int userId = getRequiredUserId(request);
        int taskId = getRequiredParameter(request, "taskId", Integer::parseInt);
        int id = getRequiredParameter(request, "id", Integer::parseInt);
        boolean highlighted = getRequiredParameter(request, "highlighted", Boolean::parseBoolean);
        log.info("Highlight {} for vacancy {} from task {} for user with id={}", highlighted ? "on" : "off", id, taskId, userId);
        service.highlight(id, taskId, userId, highlighted);
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    /**
     * Represents DO REMOVE VACANCY executor
     *
     * @param request  request
     * @param response response
     */
    private void doRemove(HttpServletRequest request, HttpServletResponse response) {
        int userId = getRequiredUserId(request);
        int taskId = getRequiredParameter(request, "taskId", Integer::parseInt);
        int id = getRequiredParameter(request, "id", Integer::parseInt);
        log.info("Remove vacancy {} from task {} for user with id={}", id, taskId, userId);
        service.delete(id, taskId, userId);
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    private int getRequiredUserId(HttpServletRequest request) {
        return requireNonNull(authManagerProvider.get().findAllowedId(request, "userId"), "No authorized");
    }
}
