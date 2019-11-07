package ru.job4j.ee.store;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import ru.job4j.ee.store.repository.dbi.JdbiProvider;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static com.google.common.io.Resources.getResource;

public class JdbiTest {
    public static void initDBI() {
        JdbiProvider.init(buildDataSource("app.properties"));
    }

    private static DataSource buildDataSource(String propertyFileName) {
        try (InputStream in = getResource(propertyFileName).openStream()) {
            Properties properties = new Properties();
            properties.load(in);
            var dataSource = new BasicDataSource();
            dataSource.setDriverClassName(properties.getProperty("db.driver"));
            dataSource.setUrl(properties.getProperty("db.url"));
            dataSource.setUsername(properties.getProperty("db.username"));
            dataSource.setPassword(properties.getProperty("db.password"));
            dataSource.setMinIdle(Integer.valueOf(properties.getProperty("db.minIdle")));
            dataSource.setMaxIdle(Integer.valueOf(properties.getProperty("db.maxIdle")));
            dataSource.setMaxTotal(Integer.valueOf(properties.getProperty("db.maxTotal")));
            dataSource.setMaxWaitMillis(Integer.valueOf(properties.getProperty("db.maxWaitMillis")));
            dataSource.setMaxOpenPreparedStatements(Integer.valueOf(properties.getProperty("db.maxOpenPreparedStatements")));
            dataSource.setInitialSize(Integer.valueOf(properties.getProperty("db.initialSize")));
            dataSource.setTestOnBorrow(Boolean.valueOf(properties.getProperty("db.testOnBorrow")));
            dataSource.setRemoveAbandonedOnBorrow(Boolean.valueOf(properties.getProperty("db.testOnBorrow")));
            dataSource.setRemoveAbandonedOnBorrow(Boolean.valueOf(properties.getProperty("db.removeAbandonedOnBorrow")));
            dataSource.setTestWhileIdle(Boolean.valueOf(properties.getProperty("db.testWhileIdle")));
            dataSource.setValidationQuery(properties.getProperty("db.validationQuery"));
            return dataSource;
        } catch (IOException e) {
            throw new IllegalStateException("cannot set JDBI connection", e);
        }
    }
}