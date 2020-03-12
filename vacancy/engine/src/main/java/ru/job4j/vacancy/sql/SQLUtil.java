package ru.job4j.vacancy.sql;

import java.sql.DriverManager;
import java.util.Map;

/**
 * Util class to handle new connections
 *
 * @author Alexander Savchenko
 * @version 1.0
 * @since 2019-08-07
 */
public class SQLUtil {
    private SQLUtil() {
        throw new IllegalStateException("should not instantiate");
    }

    public static ConnectionFactory getConnectionFactory(Map<?, ?> propertyMap) throws ClassNotFoundException {
        Class.forName((String) propertyMap.get("db.driver"));
        var url = (String) propertyMap.get("db.url");
        var username = (String) propertyMap.get("db.username");
        var password = (String) propertyMap.get("db.password");
        return () -> DriverManager.getConnection(url, username, password);
    }
}