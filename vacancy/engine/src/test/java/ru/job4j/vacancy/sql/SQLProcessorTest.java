package ru.job4j.vacancy.sql;

import org.junit.jupiter.api.Test;
import ru.job4j.vacancy.model.VacancyData;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static ru.job4j.vacancy.TestUtil.*;
import static ru.job4j.vacancy.util.TimeUtil.now;

class SQLProcessorTest {
    private static void assertMatch(ZonedDateTime expected, Timestamp actual) {
        assertEquals(Timestamp.from(expected.toInstant()), actual);
    }

    @Test
    void getLastDateAfterSaveAll() throws SQLException {
        try (SQLTestUtil.ConnectionHolder holder = SQLTestUtil.connect()) {
            SQLProcessor processor = new SQLProcessor(holder);
            ZonedDateTime now = now();
            processor.saveAll(List.of(NEW_VACANCY), now);
            Timestamp newLastDate = processor.retrieveLastDate();
            assertMatch(now, newLastDate);
        }
    }

    @Test
    void getLastDateWithError() throws SQLException {
        try (SQLTestUtil.ConnectionHolder holder = SQLTestUtil.connect()) {
            SQLProcessor processor = new SQLProcessor(holder);
            processor.saveAll(VACANCIES, now());
            holder.close(); // explicitly close to cause exception throwing
            Timestamp lastDate = processor.retrieveLastDate();
            assertNull(lastDate);
        }
    }

    @Test
    void saveAll() throws SQLException {
        try (SQLTestUtil.ConnectionHolder holder = SQLTestUtil.connect()) {
            List<VacancyData> allBefore = SQLTestUtil.getAll(holder);

            SQLProcessor processor = new SQLProcessor(holder);
            processor.saveAll(VACANCIES, now());

            List<VacancyData> allAfter = SQLTestUtil.getAll(holder);
            allAfter.removeAll(allBefore);

            assertEquals(3, allAfter.size());
            assertEquals(VACANCIES, allAfter);
        }
    }

    @Test
    void saveAllAgain() throws SQLException {
        try (SQLTestUtil.ConnectionHolder holder = SQLTestUtil.connect()) {
            List<VacancyData> allBefore = SQLTestUtil.getAll(holder);

            SQLProcessor processor = new SQLProcessor(holder);
            processor.saveAll(VACANCIES, now().minusDays(1)); // unique constraint on date column
            processor.saveAll(List.of(VACANCIES.get(0), NEW_VACANCY), now());

            List<VacancyData> allAfter = SQLTestUtil.getAll(holder);
            allAfter.removeAll(allBefore);

            assertEquals(4, allAfter.size());
            assertEquals(UPDATED_VACANCIES, allAfter);
        }
    }

    @Test
    void saveAllDuplicate() throws SQLException {
        try (SQLTestUtil.ConnectionHolder holder = SQLTestUtil.connect()) {
            SQLProcessor processor = new SQLProcessor(holder);
            processor.saveAll(VACANCIES, now().minusDays(1));
            VacancyData firstDuplicate = getDuplicate(VACANCY1);
            int amount = processor.saveAll(List.of(firstDuplicate), now());
            assertEquals(0, amount);
        }
    }

    @Test
    void saveAllWithError() throws SQLException {
        try (SQLTestUtil.ConnectionHolder holder = SQLTestUtil.connect()) {
            SQLProcessor processor = new SQLProcessor(holder);
            holder.close(); // explicitly close to cause exception throwing
            int amount = processor.saveAll(VACANCIES, now());
            assertEquals(0, amount);
        }
    }

    @Test
    void saveAllConstraintConflict() throws SQLException {
        try (SQLTestUtil.ConnectionHolder holder = SQLTestUtil.connect()) {
            SQLProcessor processor = new SQLProcessor(holder);
            ZonedDateTime now = now(); // date unique constraint
            processor.saveAll(VACANCIES, now);
            int amount = processor.saveAll(List.of(NEW_VACANCY), now);
            assertEquals(0, amount);
        }
    }
}