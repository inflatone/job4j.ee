package ru.job4j.ee.store.service.jdbi;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;

import java.sql.SQLException;

interface JdbiConnectable {
    JdbiConnectionMocker MOCKER = new JdbiConnectionMocker();

    @BeforeAll
    static void setUp() throws SQLException {
        MOCKER.start();
    }

    @AfterEach
    default void rollback() throws SQLException {
        MOCKER.rollback();
    }

    @AfterAll
    static void destroy() throws SQLException {
        MOCKER.close();
    }
}