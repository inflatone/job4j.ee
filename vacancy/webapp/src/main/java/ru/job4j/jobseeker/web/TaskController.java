package ru.job4j.jobseeker.web;

import com.google.inject.Provider;
import org.slf4j.Logger;
import ru.job4j.jobseeker.model.Task;
import ru.job4j.jobseeker.service.TaskService;
import ru.job4j.jobseeker.service.executor.ExecutionService;
import ru.job4j.jobseeker.web.security.AuthManager;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.function.Function;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.job4j.jobseeker.web.Action.*;
import static ru.job4j.jobseeker.web.WebHelper.*;
import static ru.job4j.jobseeker.web.json.JsonHelper.fromJson;

/**
 * Represents web layer of the app that serves task data requests
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2020-01-17
 */
public class TaskController extends ActionDispatcherServlet {
    private static final Logger log = getLogger(TaskController.class);

    private final TaskService service;
    private final ExecutionService executionService;

    private final Provider<AuthManager> authManagerProvider;

    @Inject
    public TaskController(TaskService service, ExecutionService executionService, Provider<AuthManager> authManagerProvider) {
        this.service = service;
        this.executionService = executionService;
        this.authManagerProvider = authManagerProvider;
    }

    @Override
    protected void fillGetActions() {
        submitGetAction(find, this::find);
        submitGetAction(empty, this::view);
    }

    @Override
    protected void fillPostActions() {
        submitPostAction(save, this::doSave);
        submitPostAction(delete, this::doRemove);
        submitPostAction(start, this::doStart);
        submitPostAction(pause, this::doPause);
        submitPostAction(empty, this::doRecount);
    }

    /**
     * Represents TASK VACANCIES LIST page redirector
     *
     * @param request  request
     * @param response response
     */
    void view(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        forwardToJsp("task", request, response);
    }

    /**
     * Represents FIND TASK/ALL executor
     *
     * @param request  request
     * @param response response
     */
    private void find(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int userId = authManagerProvider.get().getRequiredUserId(request, false);
        Integer id = getParameter(request, "id", Integer::parseInt);
        if (id == null) {
            log.info("Find all tasks for user with id={}", userId);
            var tasks = service.findAll(userId);
            asJsonToResponse(response, tasks);
        } else {
            log.info("Find task {} for user with id={}", id, userId);
            var task = service.find(id, userId);
            asJsonToResponse(response, task);
        }
    }

    /**
     * Represents CREATE/UPDATE TASK executor
     *
     * @param request  request
     * @param response response
     */
    private void doSave(HttpServletRequest request, HttpServletResponse response) throws IOException {
        var task = createModel(request);
        int userId = authManagerProvider.get().findAllowedId(() -> {
            var user = task.getUser();
            return user == null ? null : user.getId();
        });
        if (task.isNew()) {
            doCreate(task, userId);
            response.setStatus(HttpServletResponse.SC_CREATED);
        } else {
            doUpdate(task, userId);
            response.setStatus(HttpServletResponse.SC_NO_CONTENT);
        }
    }

    /**
     * Represents DELETE TASK executor
     *
     * @param request  request
     * @param response response
     */
    private void doRemove(HttpServletRequest request, HttpServletResponse response) {
        int userId = authManagerProvider.get().getRequiredUserId(request, false);
        int id = getRequiredParameter(request, "id", Integer::parseInt);
        log.info("Remove task {} for user with id={}", id, userId);
        service.delete(id, userId);
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    /**
     * Submits the given task model object to service to be stored
     *
     * @param task   task model
     * @param userId user id
     */
    private void doCreate(Task task, int userId) {
        log.info("Create task {} for user with id={}", task, userId);
        service.create(task, userId);
    }

    /**
     * Submits the given task model object to service to be updated
     *
     * @param task   task model
     * @param userId user id
     */
    private void doUpdate(Task task, int userId) {
        log.info("Update task {} for user with id={}", task, userId);
        service.update(task, userId);
    }


    /**
     * Represents START TASK NOW executor
     *
     * @param request  request
     * @param response response
     */
    private void doStart(HttpServletRequest request, HttpServletResponse response) throws IOException {
        int userId = authManagerProvider.get().getRequiredUserId(request, false);
        Integer id = getRequiredParameter(request, "id", Integer::parseInt);
        log.info("Force start task {} for user with id={}", id, userId);
        var launchLog = executionService.launch(id, userId);
        asJsonToResponse(response, launchLog);
    }

    /**
     * Represents PAUSE TASK executor
     *
     * @param request  request
     * @param response response
     */
    private void doPause(HttpServletRequest request, HttpServletResponse response) {
        int userId = authManagerProvider.get().getRequiredUserId(request, false);
        Integer id = getRequiredParameter(request, "id", Integer::parseInt);
        boolean paused = getRequiredParameter(request, "paused", Boolean::parseBoolean);
        log.info("{} task {} for user with id={}", (paused ? "Paused" : "Start"), id, userId);
        service.pause(id, userId, paused);
        response.setStatus(HttpServletResponse.SC_NO_CONTENT);
    }

    /**
     * Represents RECOUNT VACANCY AMOUNT executor
     *
     * @param request  request
     * @param response response
     */
    private void doRecount(HttpServletRequest request, HttpServletResponse response) {
        int userId = authManagerProvider.get().getRequiredUserId(request, false);
        var param = getRequiredParameter(request, "recount", Function.identity());
        switch (param) {
            case "amount":
                log.info("Recount parsed vacancies amount for user with id={}", userId);
                service.recount(userId);
                break;
            case "launch":
                log.info("Recount vacancies next launch for user with id={}", userId);
                service.recountNextLaunches(userId);
                break;
        }
    }

    /**
     * Wraps the given data from the servlet request in a transfer object
     *
     * @param request request
     * @return task transfer object
     */
    Task createModel(HttpServletRequest request) throws IOException {
        return fromJson(request.getReader(), Task.class);
    }
}
