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
import static ru.job4j.jobseeker.web.Action.find;
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

    private void find(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int userId = getRequiredUserId(request);
        int taskId = getRequiredParameter(request, "taskId", Integer::parseInt);
        log.info("Find all vacancies from task with id={} for user with id={}", taskId, userId);
        var vacancies = service.findAll(taskId, userId);
        asJsonToResponse(response, vacancies);
    }

    private int getRequiredUserId(HttpServletRequest request) {
        return requireNonNull(authManagerProvider.get().findAllowedId(request, "userId"), "No authorized");
    }
}
