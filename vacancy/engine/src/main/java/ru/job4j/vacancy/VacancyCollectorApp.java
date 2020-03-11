package ru.job4j.vacancy;

import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.vacancy.job.VacancyCollectorJob;

/**
 * Main executable class to start sql.ru vacancy parser
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-07-27
 */
public class VacancyCollectorApp {
    private static final Logger LOG = LoggerFactory.getLogger(VacancyCollectorApp.class);

    public static void main(String[] args) {
        VacancyCollector vacancyCollector = new VacancyCollector(args);
        try {
            var message = vacancyCollector.start(VacancyCollectorJob.class);
            LOG.info(message);
        } catch (Exception e) {
            vacancyCollector.handleException(LOG, e);
            try {
                vacancyCollector.shutdown(false);
            } catch (SchedulerException ex) {
                // ignored
            }
        }
    }
}
