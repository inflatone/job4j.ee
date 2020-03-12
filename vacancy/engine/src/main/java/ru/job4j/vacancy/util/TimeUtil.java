package ru.job4j.vacancy.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

import static ru.job4j.vacancy.util.ExceptionUtil.nullSafely;

/**
 * Contains utility logic to work with date-time
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-08-11
 */
public class TimeUtil {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-d H:mm");

    /**
     * should not be initialized
     */
    private TimeUtil() {
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

    public static String asLine(Date dateTime) {
        return asLine(toZonedDateTime(dateTime));
    }

    public static String asLine(TemporalAccessor dateTime) {
        return nullSafely(dateTime, FORMATTER::format);
    }

    public static ZonedDateTime toZonedDateTime(String line) {
        return LocalDateTime.parse(line, FORMATTER).atZone(ZoneId.systemDefault());
    }

    public static ZonedDateTime toZonedDateTime(Date date) {
        return nullSafely(date, d -> d.toInstant().atZone(ZoneId.systemDefault()));
    }

    public static Date toDate(String line) {
        return Date.from(toZonedDateTime(line).toInstant());
    }
}