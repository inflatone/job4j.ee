package ru.job4j.jobseeker.model;

import lombok.AllArgsConstructor;
import ru.job4j.vacancy.util.TimeUtil;

import java.time.ZonedDateTime;
import java.util.Date;

/**
 * Repeat rule
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2020-01-16
 */
@AllArgsConstructor
public enum RepeatRule {
    hourly("%d %d 0/1 1/1 * ? *") {
        @Override
        Object[] asArgs(ZonedDateTime date) {
            return new Object[]{date.getSecond(), date.getMinute()};
        }
    },
    daily("%d %d %d 1/1 * ? *") {
        @Override
        Object[] asArgs(ZonedDateTime date) {
            return new Object[]{date.getSecond(), date.getMinute(), date.getHour()};
        }
    },
    weekly("%d %d %d ? * %3.3s *") {
        @Override
        Object[] asArgs(ZonedDateTime date) {
            return new Object[]{date.getSecond(), date.getMinute(), date.getHour(), date.getDayOfWeek()};
        }
    },
    monthly("%d %d %d %d 1/1 ? *") {
        @Override
        Object[] asArgs(ZonedDateTime date) {
            return new Object[]{date.getSecond(), date.getMinute(), date.getHour(), date.getDayOfMonth()};
        }
    },
    manually(null) {
        @Override
        public String cronExpression(Date date) {
            return null;
        }
    };

    private final String format;

    /**
     * Composes the cron expression based on the given date as the cron core
     *
     * @param date date
     * @return cron expression
     */
    public String cronExpression(Date date) {
        var dateTime = TimeUtil.toZonedDateTime(date);
        return String.format(format, asArgs(dateTime));
    }

    /**
     * Returns the given date's 'parts' (such as minutes, day-of-month, etc) that are required to compose a cron expression
     *
     * @param date date
     * @return date 'parts'
     */
    Object[] asArgs(ZonedDateTime date) {
        return new Object[0];
    }
}