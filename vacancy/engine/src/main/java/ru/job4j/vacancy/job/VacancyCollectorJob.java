package ru.job4j.vacancy.job;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.vacancy.jsoup.JsoupProcessor;
import ru.job4j.vacancy.jsoup.ParseParameters;
import ru.job4j.vacancy.model.VacancyData;
import ru.job4j.vacancy.sql.SQLProcessor;
import ru.job4j.vacancy.util.TimeUtil;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import static java.util.Objects.requireNonNull;
import static ru.job4j.vacancy.jsoup.ParserUtil.buildParseParameters;
import static ru.job4j.vacancy.util.TimeUtil.*;

/**
 * Implements 'job' class to be executed by job scheduler
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-07-27
 */
public class VacancyCollectorJob implements ExecutorJob {
    public static final String VACANCY_KEYWORD = "vacancy.keyword";
    public static final String VACANCY_CITY = "vacancy.city";

    private static final Logger log = LoggerFactory.getLogger(VacancyCollectorJob.class);

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
        ZonedDateTime now = now();
        log.info("[{}] starts", context.getJobDetail().getKey().getName());
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();

        var sqlProcessor = (SQLProcessor) dataMap.get(SQLProcessor.class.getSimpleName());
        requireNonNull(sqlProcessor, "Sql processor was missing");
        var dateLimit = computeLastExecuteDate(sqlProcessor);

        var jsoupProcessor = (JsoupProcessor) dataMap.get(JsoupProcessor.class.getSimpleName());
        requireNonNull(jsoupProcessor, "Jsoup processor was missing");

        ParseParameters params = buildParseParameters(dataMap, dateLimit);
        List<VacancyData> vacancies = jsoupProcessor.parseVacancies(params);

        int amount = sqlProcessor.saveAll(vacancies, now);
        log.info("Stored {} new vacancy(ies) out of {}", amount, vacancies.size());
        log.info("Next vacancy scan starts at {}" + TimeUtil.asLine(context.getNextFireTime()));
    }

    private ZonedDateTime computeLastExecuteDate(SQLProcessor processor) {
        var logDate = processor.retrieveLastDate();
        return logDate != null ? ZonedDateTime.ofInstant(logDate.toInstant(), ZoneId.systemDefault()) : firstDayOfYear();
    }

    @Override
    public List<String> getRequiredKeys() {
        return List.of(SQLProcessor.class.getSimpleName(), JsoupProcessor.class.getSimpleName(), VACANCY_KEYWORD);
    }

    @Override
    public List<String> getAdditionalKeys() {
        return List.of(VACANCY_CITY);
    }
}