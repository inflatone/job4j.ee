package ru.job4j.vacancy.sql;

import one.util.streamex.IntStreamEx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.vacancy.model.VacancyData;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static ru.job4j.vacancy.util.Util.firstDayOfYear;

/**
 * Represents program logic of storing parsed data from sql.ru
 * Also provides another required operations with the DB
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-07-29
 */
public class SQLProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(SQLProcessor.class);
    private final ConnectionFactory connectionFactory;

    public SQLProcessor(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    /**
     * Gets from the DB the date of the last vacancy collecting
     *
     * @return last date
     */
    public LocalDateTime lastExecuteDate() {
        LocalDateTime result = firstDayOfYear();
        try (var connection = connectionFactory.getConnection();
             var statement = connection.createStatement();
             var resultSet = statement.executeQuery("SELECT MAX(date) FROM log")
        ) {
            resultSet.next();
            var date = resultSet.getTimestamp("max");
            result = date != null ? date.toLocalDateTime() : result;

        } catch (SQLException e) {
            LOG.error("error due to get database update last date", e);
        }
        return result;
    }

    /**
     * Adds the parsed vacancies in the DB, also records in the DB date where parsing was start
     *
     * @param vacancies parsed vacancies
     * @return number of vacancies that have been added
     */
    public int addAll(List<VacancyData> vacancies, LocalDateTime now) {
        int vacancyAmount = 0;
        try (var connection = connectionFactory.getConnection();
             var vacancyStatement = connection.prepareStatement(
                     "INSERT INTO vacancy_data (title, description, link, date) VALUES (?, ?, ?, ?) "
                             + "ON CONFLICT (title) DO NOTHING"); // https://habr.com/ru/post/264281/
             var timeCheckStatement = connection.prepareStatement("INSERT INTO log (date, amount) VALUES (?, ?)")
        ) {
            try {
                connection.setAutoCommit(false);
                fillVacancyStatement(vacancyStatement, vacancies);

                var ints = vacancyStatement.executeBatch();
                vacancyAmount = IntStreamEx.of(ints).sum();

                timeCheckStatement.setTimestamp(1, Timestamp.valueOf(now));
                timeCheckStatement.setInt(2, vacancyAmount);
                timeCheckStatement.execute();

                connection.commit();
                LOG.info("add to db {} new vacancies", vacancyAmount);
            } catch (SQLException e) {
                connection.rollback();
                vacancyAmount = 0;
                LOG.error("error due to update database", e);
            }
        } catch (SQLException e) {
            LOG.error("error due to create a connection to database", e);
        }
        return vacancyAmount;
    }

    /**
     * Fills prepared statement with all submitted vacancies
     *
     * @param statement prepared statement
     * @param vacancies vacancies to be added
     * @throws SQLException if sql connection error occurs
     */
    private void fillVacancyStatement(PreparedStatement statement, List<VacancyData> vacancies) throws SQLException {
        for (VacancyData vac : vacancies) {
            statement.setString(1, vac.getTitle());
            statement.setString(2, vac.getDescription());
            statement.setString(3, vac.getUrl());
            statement.setTimestamp(4, Timestamp.valueOf(vac.getDateTime()));
            statement.addBatch();
        }
    }
}
