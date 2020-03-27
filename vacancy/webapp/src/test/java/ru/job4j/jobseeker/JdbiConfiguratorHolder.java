package ru.job4j.jobseeker;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import ru.job4j.jobseeker.dao.JdbiConfigurator;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import static com.google.common.io.Resources.getResource;
import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;
import static org.mockito.Mockito.*;
import static ru.job4j.jobseeker.inject.DaoHsqldbModule.SQL_RESOURCE_PATH;

public class JdbiConfiguratorHolder {
    private static final String PROPERTY_RESOURCE_NAME = "db/test.properties";
    private static final String SQL_POPULATE_TEST_PATH = "db/hsqldb";

    private static JdbiConfigurator jdbiConfigurator;

    private static DataSource realDataSource;

    private static Connection singletonConnection;

    public static JdbiConfigurator getJdbiConfigurator() {
        return jdbiConfigurator;
    }

    public static void start() throws SQLException {
        singletonConnection = getSingletonConnection();
    }

    public static void rollback() throws SQLException {
        singletonConnection.rollback();
    }

    public static void close() throws SQLException {
        singletonConnection.rollback();
        singletonConnection.close();
    }

    private static Connection getSingletonConnection() throws SQLException {
        if (realDataSource == null) {
            initJdbiBySingletonConnectionDataSource();
        } else if (singletonConnection.isClosed()) {
            singletonConnection = newSingletonConnection();
        }
        return singletonConnection;
    }

    private static void initJdbiBySingletonConnectionDataSource() throws SQLException {
        realDataSource = buildDataSource();
        singletonConnection = newSingletonConnection();

        var mockSingleConnection = spy(singletonConnection);
        doNothing().when(mockSingleConnection).close();

        var mockDataSource = spy(realDataSource);
        doReturn(mockSingleConnection).when(mockDataSource).getConnection();

        jdbiConfigurator = new JdbiConfigurator(mockDataSource)
                .buildUpDatabase(SQL_RESOURCE_PATH) // main structure
                .buildUpDatabase(SQL_POPULATE_TEST_PATH);  // populate task and vacancy by test data

        mockSingleConnection.commit(); // commit DB built structure

        doNothing().when(mockSingleConnection).commit(); // no need to commit anything in future after db set up
    }

    private static Connection newSingletonConnection() throws SQLException {
        var connection = realDataSource.getConnection();
        connection.setAutoCommit(false);
        return connection;
    }

    private static DataSource buildDataSource() {
        try (InputStream in = getResource(PROPERTY_RESOURCE_NAME).openStream()) {
            Properties properties = new Properties();
            properties.load(in);
            var dataSource = new BasicDataSource();
            dataSource.setDriverClassName(properties.getProperty("db.driver"));
            dataSource.setUrl(properties.getProperty("db.url"));
            dataSource.setUsername(properties.getProperty("db.username"));
            dataSource.setPassword(properties.getProperty("db.password"));
            dataSource.setMinIdle(parseInt(properties.getProperty("db.minIdle")));
            dataSource.setMaxIdle(parseInt(properties.getProperty("db.maxIdle")));
            dataSource.setMaxTotal(parseInt(properties.getProperty("db.maxTotal")));
            dataSource.setMaxWaitMillis(parseInt(properties.getProperty("db.maxWaitMillis")));
            dataSource.setMaxOpenPreparedStatements(parseInt(properties.getProperty("db.maxOpenPreparedStatements")));
            dataSource.setInitialSize(parseInt(properties.getProperty("db.initialSize")));
            dataSource.setTestOnBorrow(parseBoolean(properties.getProperty("db.testOnBorrow")));
            dataSource.setRemoveAbandonedOnBorrow(parseBoolean(properties.getProperty("db.testOnBorrow")));
            dataSource.setRemoveAbandonedOnBorrow(parseBoolean(properties.getProperty("db.removeAbandonedOnBorrow")));
            dataSource.setTestWhileIdle(parseBoolean(properties.getProperty("db.testWhileIdle")));
            dataSource.setValidationQuery(properties.getProperty("db.validationQuery"));
            return dataSource;
        } catch (IOException e) {
            throw new IllegalStateException("cannot set JDBI connection", e);
        }
    }
}