package ru.job4j.ee.store;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static com.google.common.io.Resources.getResource;
import static java.lang.Boolean.parseBoolean;
import static java.lang.Integer.parseInt;

public class JdbiUtil {
    private static final String PROPERTY_RESOURCE_NAME = "app.properties";

    private JdbiUtil() {
        throw new IllegalStateException("should not be instantiated");
    }

    public static DataSource buildDataSource() {
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