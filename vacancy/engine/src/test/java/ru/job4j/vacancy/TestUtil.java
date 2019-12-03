package ru.job4j.vacancy;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import ru.job4j.vacancy.model.VacancyData;
import ru.job4j.vacancy.sql.ConnectionFactory;
import ru.job4j.vacancy.sql.SQLUtil;

import java.lang.reflect.Proxy;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.io.Resources.getResource;
import static java.time.LocalDateTime.of;
import static java.time.Month.JULY;
import static java.time.Month.JUNE;
import static ru.job4j.vacancy.util.Util.now;

public class TestUtil {
    public static final VacancyData VACANCY1 = new VacancyData("first", "link_1", "description_1", of(2019, JUNE, 30, 15, 17));
    public static final VacancyData VACANCY2 = new VacancyData("second", "link_2", "description_2", of(2019, JUNE, 28, 10, 13));
    public static final VacancyData VACANCY3 = new VacancyData("third", "link_3", "description_3", of(2019, JUNE, 26, 21, 48));
    public static final VacancyData NEW_VACANCY = new VacancyData("new", "new_link", "new_description", of(2019, JULY, 2, 18, 0));

    public static final List<VacancyData> VACANCIES = List.of(VACANCY1, VACANCY2, VACANCY3);

    public static final List<VacancyData> UPDATED_VACANCIES = List.of(NEW_VACANCY, VACANCY1, VACANCY2, VACANCY3);

    public static final LocalDateTime LIMIT_DATE = LocalDateTime.of(2019, JULY, 25, 16, 0, 0);

    public static VacancyData getDuplicate(VacancyData vacancy) {
        return new VacancyData(vacancy.getTitle(), "new_" + vacancy.getUrl(), "new_" + vacancy.getDescription(), now());
    }

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
                resultSet.getTimestamp("date").toLocalDateTime()
        );
    }

    public static ConnectionHolder connect() throws SQLException {
        return new ConnectionHolder("job.properties");
    }

    public static String asFullPathString(String resourceName) throws URISyntaxException {
        return Paths.get(getResource(resourceName).toURI()).toString();
    }

    static class ConnectionHolder implements ConnectionFactory, AutoCloseable {
        private Connection connection;

        public ConnectionHolder(String resource) throws SQLException {
            this.connection = SQLUtil.getConnection(resource);
        }

        @Override
        public Connection getConnection() throws SQLException {
            return rollbackProxy(connection);
        }

        @Override
        public void close() throws SQLException {
            if (connection != null) {
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

    static class JobExample implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            // do nothing
        }
    }
}