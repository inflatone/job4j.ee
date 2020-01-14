package ru.job4j.vacancy.sql;

import ru.job4j.vacancy.model.VacancyData;

import java.io.InputStream;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static com.google.common.io.Resources.getResource;

class SQLTestUtil {
    public static List<VacancyData> getAll(ConnectionFactory connectionFactory) {
        List<VacancyData> result = new ArrayList<>();
        try (var connection = connectionFactory.getConnection();
             var selectStatement = connection.createStatement()
        ) {
            ResultSet resultSet = selectStatement.executeQuery("SELECT * FROM  vacancy_data ORDER BY date DESC");
            while (resultSet.next()) {
                result.add(parseVacancy(resultSet));
            }

        } catch (SQLException e) {
            throw new IllegalStateException("cannot connect to db", e);
        }
        return result;
    }

    private static VacancyData parseVacancy(ResultSet resultSet) throws SQLException {
        return new VacancyData(
                resultSet.getString("title"),
                resultSet.getString("link"),
                resultSet.getString("description"),
                resultSet.getTimestamp("date").toInstant().atZone(ZoneId.systemDefault())
        );
    }

    public static ConnectionHolder connect() throws SQLException {
        return new ConnectionHolder("job.properties");
    }

    public static class ConnectionHolder implements ConnectionFactory, AutoCloseable {
        private final Connection connection;

        public ConnectionHolder(String resource) throws SQLException {
            this.connection = getConnection(resource);
        }

        private static Connection getConnection(String resource) throws SQLException {
            return getConnectionFactory(resource).getConnection();
        }

        @Override
        public Connection getConnection() throws SQLException {
            return rollbackProxy(connection);
        }

        @Override
        public void close() throws SQLException {
            if (connection != null && !connection.isClosed()) {
                if (!connection.getAutoCommit()) {
                    connection.rollback();
                }
                connection.close();
            }
        }

        public static Connection rollbackProxy(Connection connection) throws SQLException {
            connection.setAutoCommit(false);
            return (Connection) Proxy.newProxyInstance(
                    ConnectionHolder.class.getClassLoader(),
                    new Class[]{Connection.class},
                    (proxy, method, args) -> {
                        Object result = null;
                        // do not commit and do not close (handle it on ConnectionHolder.close() call)
                        if (!"commit".equals(method.getName()) && !"close".equals(method.getName())) {
                            result = method.invoke(connection, args);
                        }
                        return result;
                    }
            );
        }
    }

    public static ConnectionFactory getConnectionFactory(String resource) {
        try (InputStream in = getResource(resource).openStream()) {
            Properties properties = new Properties();
            properties.load(in);
            return SQLUtil.getConnectionFactory(properties);
        } catch (Exception e) {
            throw new IllegalArgumentException("cannot load connection properties from " + resource, e);
        }
    }
}