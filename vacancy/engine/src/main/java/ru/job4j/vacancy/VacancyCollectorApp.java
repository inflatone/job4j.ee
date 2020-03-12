package ru.job4j.vacancy;

import org.quartz.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.vacancy.util.ExceptionUtil;

import java.util.Date;

import static ru.job4j.vacancy.util.TimeUtil.asLine;

/**
 * Main executable class to start sql.ru vacancy parser
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-07-27
 */
public class VacancyCollectorApp {
    private static final Logger log = LoggerFactory.getLogger(VacancyCollectorApp.class);

    public static void main(String[] args) {
        var date = new Date();
        VacancyCollector vacancyCollector = new VacancyCollector(args);
        try {
            Trigger trigger = vacancyCollector.start();
            String formattedDateTime = asLine(trigger.getFireTimeAfter(date));
            log.info("Next vacancy scan has been scheduled on " + formattedDateTime);
        } catch (Exception e) {
            vacancyCollector.handleException(log, e);
            ExceptionUtil.handleRun(() -> vacancyCollector.shutdown(false));
        }
    }
}
