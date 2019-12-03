package ru.job4j.vacancy.job;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import ru.job4j.vacancy.model.VacancyData;
import ru.job4j.vacancy.sql.SQLProcessor;

import java.time.LocalDateTime;
import java.util.List;

import static ru.job4j.vacancy.util.JsoupHelper.specifyJsoupProcessor;
import static ru.job4j.vacancy.util.Util.getConnectionFactory;
import static ru.job4j.vacancy.util.Util.now;

/**
 * Implements 'job' class to be executed by job scheduler
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-07-27
 */
public class VacancyCollectorJob implements Job {
    /**
     * Executes one cycle of sql.ru vacancy collector work.
     * <p>
     * Creates connection factory, checks last time vacancy table was updated,
     * then starts html parser work and stores its result in the DB
     *
     * @param context job executor context
     */
    @Override
    public void execute(JobExecutionContext context) {
        // inits on method start to prevent program from skipping new vacancies which would be added during the DB update
        LocalDateTime now = now();
        SQLProcessor sqlProcessor = new SQLProcessor(getConnectionFactory(context));
        var dateLimit = sqlProcessor.lastExecuteDate();
        List<VacancyData> vacancies = specifyJsoupProcessor(context).parseVacancies(dateLimit);
        sqlProcessor.addAll(vacancies, now);
    }
}
