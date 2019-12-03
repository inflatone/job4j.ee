package ru.job4j.vacancy.sql;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Represents functional interface to wrap submitted connection objects
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-08-07
 */
@FunctionalInterface
public interface ConnectionProxy {
    Connection proxy(Connection connection) throws SQLException;
}
