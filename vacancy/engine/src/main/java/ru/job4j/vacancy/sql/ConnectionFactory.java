package ru.job4j.vacancy.sql;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Represents functional interface to get new connection to DB
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-08-07
 */
@FunctionalInterface
public interface ConnectionFactory {
    Connection getConnection() throws SQLException;
}
