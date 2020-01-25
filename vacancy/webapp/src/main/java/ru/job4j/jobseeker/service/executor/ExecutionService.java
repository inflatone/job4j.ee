package ru.job4j.jobseeker.service.executor;

import org.slf4j.Logger;
import ru.job4j.jobseeker.dao.TaskDao;
import ru.job4j.jobseeker.dao.VacancyDao;
import ru.job4j.jobseeker.model.LaunchLog;
import ru.job4j.jobseeker.model.Task;
import ru.job4j.vacancy.jsoup.JsoupProcessor;
import ru.job4j.vacancy.jsoup.ParseParameters;
import ru.job4j.vacancy.model.VacancyData;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static org.slf4j.LoggerFactory.getLogger;
import static ru.job4j.jobseeker.service.ValidationHelper.checkNotFoundEntityWithId;
import static ru.job4j.vacancy.util.TimeUtil.toZonedDateTime;

/**
 * Represents task execution service layer
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2020-01-25
 */
public class ExecutionService {
    private static final Logger log = getLogger(ExecutionService.class);

    private final JsoupProcessor processor;

    private final TaskDao taskDao;
    private final VacancyDao vacancyDao;

    @Inject
    public ExecutionService(JsoupProcessor processor, TaskDao taskDao, VacancyDao vacancyDao) {
        this.processor = processor;
        this.taskDao = taskDao;
        this.vacancyDao = vacancyDao;
    }

    public LaunchLog launch(int taskId, int userId) {
        var task = checkNotFoundEntityWithId(taskDao.find(taskId, userId), taskId);
        Date now = new Date();
        var params = buildParseParameters(task);
        LaunchLog launchLog;
        try {
            List<VacancyData> vacancies = processor.parseVacancies(params);
            List<VacancyData> duplicated = vacancyDao.saveAll(vacancies, task);

            launchLog = LaunchLog.success(vacancies.size(), duplicated.size(), task);
            updateAfterLaunch(launchLog, now);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            launchLog = LaunchLog.fail(task);
        }
        taskDao.saveLog(launchLog, task.getId());
        return launchLog;
    }

    private void updateAfterLaunch(LaunchLog launchLog, Date now) {
        var task = launchLog.getTask();
        // TODO update setLaunch for next time
        task.setLimit(now);
        task.setAmount(task.getAmount() + launchLog.getAddedAmount());
        taskDao.innerUpdate(task);
    }

    private static ParseParameters buildParseParameters(Task task) {
        return ParseParameters.of(Set.of(task.getSource().getTitle()), task.getKeyword(), task.getCity(), toZonedDateTime(task.getLimit()));
    }

}