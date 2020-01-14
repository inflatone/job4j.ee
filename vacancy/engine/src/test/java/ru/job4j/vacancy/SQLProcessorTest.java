package ru.job4j.vacancy;

import org.junit.jupiter.api.Test;
import ru.job4j.vacancy.model.VacancyData;
import ru.job4j.vacancy.sql.SQLProcessor;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.job4j.vacancy.TestUtil.*;
import static ru.job4j.vacancy.util.Util.firstDayOfYear;
import static ru.job4j.vacancy.util.Util.now;

public class SQLProcessorTest {
    @Test
    public void testGetLastDateFirstTime() throws SQLException {
        try (ConnectionHolder holder = connect()) {
            SQLProcessor processor = new SQLProcessor(holder);
            LocalDateTime lastDate = processor.lastExecuteDate();
            assertEquals(firstDayOfYear(), lastDate);
        }
    }

    @Test
    public void testGetLastDateAfterCollect() throws SQLException {
        try (ConnectionHolder holder = connect()) {
            SQLProcessor processor = new SQLProcessor(holder);
            LocalDateTime now = now();
            processor.addAll(List.of(NEW_VACANCY), now);
            LocalDateTime newLastDate = processor.lastExecuteDate();
            assertEquals(now, newLastDate);
        }
    }

    @Test
    public void testGetLastDateWithError() throws SQLException {
        ConnectionHolder holder = connect();
        SQLProcessor processor = new SQLProcessor(holder);
        processor.addAll(VACANCIES, now());
        holder.close();
        LocalDateTime lastDate = processor.lastExecuteDate();
        assertEquals(firstDayOfYear(), lastDate);
    }

    @Test
    public void testAddAll() throws SQLException {
        try (ConnectionHolder holder = connect()) {
            SQLProcessor processor = new SQLProcessor(holder);
            processor.addAll(VACANCIES, now());

            List<VacancyData> all = getAll(holder);
            assertEquals(3, all.size());
            assertEquals(VACANCIES, all);
        }
    }

    @Test
    public void testAddAllAgain() throws SQLException {
        try (ConnectionHolder holder = connect()) {
            SQLProcessor processor = new SQLProcessor(holder);
            processor.addAll(VACANCIES, now().minusDays(1)); // unique constraint on date column
            processor.addAll(List.of(VACANCIES.get(0), NEW_VACANCY), now());
            List<VacancyData> all = getAll(holder);
            assertEquals(4, all.size());
            assertEquals(UPDATED_VACANCIES, all);
        }
    }

    @Test
    public void testAddAllDuplicate() throws SQLException {
        try (ConnectionHolder holder = connect()) {
            SQLProcessor processor = new SQLProcessor(holder);
            processor.addAll(VACANCIES, now().minusDays(1));
            VacancyData firstDuplicate = getDuplicate(VACANCY1);
            int amount = processor.addAll(List.of(firstDuplicate), now());
            assertEquals(0, amount);
        }
    }

    @Test
    public void testAddAllWithError() throws SQLException {
        ConnectionHolder holder = connect();
        SQLProcessor processor = new SQLProcessor(holder);
        holder.close();
        int amount = processor.addAll(VACANCIES, now());
        assertEquals(0, amount);
    }

    @Test
    public void testAddAllConstraintConflict() throws SQLException {
        try (ConnectionHolder holder = connect()) {
            SQLProcessor processor = new SQLProcessor(holder);
            LocalDateTime now = now(); // date unique constraint
            processor.addAll(VACANCIES, now);
            int amount = processor.addAll(List.of(NEW_VACANCY), now);
            assertEquals(0, amount);
        }
    }
}