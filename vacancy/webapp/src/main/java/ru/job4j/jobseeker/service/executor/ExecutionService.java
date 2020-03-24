package ru.job4j.jobseeker.service.executor;

import lombok.extern.slf4j.Slf4j;
import ru.job4j.jobseeker.dao.TaskDao;
import ru.job4j.jobseeker.dao.VacancyDao;
import ru.job4j.jobseeker.model.LaunchLog;
import ru.job4j.vacancy.jsoup.JsoupProcessor;
import ru.job4j.vacancy.model.VacancyData;
import ru.job4j.vacancy.util.ExceptionUtil;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;

import static ru.job4j.jobseeker.service.ValidationHelper.checkNotFoundEntityWithId;
import static ru.job4j.jobseeker.service.executor.ExecutionHelper.buildParseParameters;
import static ru.job4j.jobseeker.service.executor.ExecutionHelper.code;

/**
 * Represents task execution service layer
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2020-01-25
 */
@Slf4j
public class ExecutionService implements TaskLauncher {
    private final WebJobExecutor executor;
    private final JsoupProcessor processor;

    private final TaskDao taskDao;
    private final VacancyDao vacancyDao;

    @Inject
    public ExecutionService(WebJobExecutor executor, JsoupProcessor processor, TaskDao taskDao, VacancyDao vacancyDao) {
        this.executor = executor;
        this.processor = processor;
        this.taskDao = taskDao;
        this.vacancyDao = vacancyDao;
    }

    @Override
    public LaunchLog launch(int taskId, int userId) {
        log.info("Task with id={} is launched", taskId);
        var task = checkNotFoundEntityWithId(taskDao.find(taskId, userId), taskId);
        var now = new Date();
        var params = buildParseParameters(task);
        LaunchLog launchLog;
        try {
            List<VacancyData> vacancies = processor.parseVacancies(params);
            List<VacancyData> duplicated = vacancyDao.saveAll(vacancies, task);

            launchLog = LaunchLog.success(vacancies.size(), duplicated.size(), task);
            updateAfterLaunch(launchLog, now);
            log.info("Start of task with id={} is successfully completed", taskId);
        } catch (Exception e) {
            log.error("Start of task with id=" + taskId + " fails with exception: " + ExceptionUtil.asLine(e), e);
            launchLog = LaunchLog.fail(task);
        }
        taskDao.saveLog(launchLog, task.getId());
        return launchLog;
    }

    private void updateAfterLaunch(LaunchLog launchLog, Date now) {
        var task = launchLog.getTask();
        task.setLaunch(executor.getNextLaunch(code(task.getId())));
        task.setLimit(now);
        task.setAmount(task.getAmount() + launchLog.getAddedAmount());
        taskDao.innerUpdate(task);
    }
}