package ru.job4j.vacancy.util;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

import static java.time.temporal.ChronoUnit.MINUTES;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TimeUtilTest {
    @Test
    void toZonedDate() {
        Date actual = TimeUtil.toDate("2019-02-21 5:13");
        var expected = ZonedDateTime.of(
                LocalDateTime.of(2019, 2, 21, 5, 13), ZoneId.systemDefault());
        assertMatch(actual, expected);
    }

    public static void assertMatch(Date actualDate, ZonedDateTime expected) {
        actualDate.toInstant();
        ZonedDateTime actual = ZonedDateTime.ofInstant(actualDate.toInstant(), ZoneId.systemDefault()).truncatedTo(MINUTES);
        assertEquals(expected, actual);
    }
}
