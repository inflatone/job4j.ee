package ru.job4j.vacancy.util;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import ru.job4j.vacancy.sql.ConnectionFactory;
import ru.job4j.vacancy.sql.SQLUtil;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;

/**
 * Contains utility logic to work with date-time
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-08-11
 */
public class Util {
    /**
     * should not be initialized
     */
    private Util() {
    }

    public static ZonedDateTime now() {
        return ZonedDateTime.now().truncatedTo(ChronoUnit.MINUTES);
    }

    /**
     * Gets the date all vacancies which date's after the one should be parsed first time
     *
     * @return date-before limiter
     */
    public static ZonedDateTime firstDayOfYear() {
        return ZonedDateTime.now().with(TemporalAdjusters.firstDayOfYear()).truncatedTo(ChronoUnit.DAYS);
    }

    /**
     * Checks if the checked date is after (or equals) date limit
     *
     * @param checked checked date
     * @param limit   date to compare
     * @return true if checked date is under the date limit
     */
    public static boolean isNotBefore(LocalDateTime checked, LocalDateTime limit) {
        // to prevent program from skipping vacations posted on dateTime = last check dateTime
        return checked.compareTo(limit) >= 0;
    }

    /**
     * Extract required parameters from the context and inits connection factory
     * which returns new connection based on specified credentials
     *
     * @param context context
     * @return connection factory
     */
    public static ConnectionFactory getConnectionFactory(JobExecutionContext context) {
        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        try {
            return SQLUtil.getConnectionFactory(dataMap);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("JDBC driver not found", e);
        }
    }

}