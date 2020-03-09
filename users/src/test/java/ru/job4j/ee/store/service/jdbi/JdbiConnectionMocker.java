package ru.job4j.ee.store.service.jdbi;

import ru.job4j.ee.store.repository.dbi.JdbiProvider;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

import static org.mockito.Mockito.*;
import static ru.job4j.ee.store.JdbiUtil.buildDataSource;

class JdbiConnectionMocker {
    private static DataSource realDataSource;

    private Connection singletonConnection;

    public void start() throws SQLException {
        singletonConnection = getSingletonConnection();
    }

    public void rollback() throws SQLException {
        singletonConnection.rollback();
    }

    public void close() throws SQLException {
        singletonConnection.rollback();
        singletonConnection.close();
    }

    private Connection getSingletonConnection() throws SQLException {
        if (realDataSource == null) {
            initJdbiBySingletonConnectionDataSource();
        } else if (singletonConnection.isClosed()) {
            singletonConnection = newSingletonConnection();
        }
        return singletonConnection;
    }

    private void initJdbiBySingletonConnectionDataSource() throws SQLException {
        realDataSource = buildDataSource();
        singletonConnection = newSingletonConnection();

        var mockSingleConnection = spy(singletonConnection);
        doNothing().when(mockSingleConnection).close();
        doNothing().when(mockSingleConnection).commit();

        var mockDataSource = spy(realDataSource);
        doReturn(mockSingleConnection).when(mockDataSource).getConnection();
        JdbiProvider.init(mockDataSource);
    }

    private static Connection newSingletonConnection() throws SQLException {
        var connection = realDataSource.getConnection();
        connection.setAutoCommit(false);
        return connection;
    }
}
