package ru.job4j.vacancy.sql;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import static com.google.common.io.Resources.getResource;

/**
 * Util class to handle new connections
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-08-07
 */
public class SQLUtil {
    /**
     * should not instantiate
     */
    private SQLUtil() {
    }

    public static Connection getConnection(String resource) throws SQLException {
        return getConnection(resource, null);
    }

    public static Connection getConnection(String resource, ConnectionProxy proxy) throws SQLException {
        return getConnectionFactory(resource, proxy).getConnection();
    }

    /**
     * Creates and returns new connection factory based on connection properties submitted by the resource file
     *
     * @param resource connection properties file path
     * @return connection factory
     */
    public static ConnectionFactory getConnectionFactory(String resource) {
        return getConnectionFactory(resource, null);
    }

    public static ConnectionFactory getConnectionFactory(String resource, ConnectionProxy proxy) {
        try (InputStream in = getResource(resource).openStream()) {
            Properties properties = new Properties();
            properties.load(in);
            if (proxy != null) {
                properties.put("proxy", proxy);
            }
            return getConnectionFactory(properties);
        } catch (Exception e) {
            throw new IllegalArgumentException("cannot load connection properties from " + resource, e);
        }
    }

    public static ConnectionFactory getConnectionFactory(Map propertyMap) throws ClassNotFoundException {
        Class.forName((String) propertyMap.get("db.driver"));
        var url = (String) propertyMap.get("db.url");
        var username = (String) propertyMap.get("db.username");
        var password = (String) propertyMap.get("db.password");
        return () -> getConnection(url, username, password, (ConnectionProxy) propertyMap.get("proxy"));
    }

    private static Connection getConnection(String url, String username, String password, ConnectionProxy proxy) throws SQLException {
        Connection connection = DriverManager.getConnection(url, username, password);
        return proxy == null ? connection : proxy.proxy(connection);
    }

    public interface SQLExecutor<T> {
        T execute(Connection connection) throws SQLException;
    }

    public interface ResultSetHandler<T> {
        T execute(ResultSet resultSet) throws SQLException;
    }
}

