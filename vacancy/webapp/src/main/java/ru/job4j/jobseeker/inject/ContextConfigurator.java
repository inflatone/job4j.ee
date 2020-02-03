package ru.job4j.jobseeker.inject;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import org.slf4j.Logger;
import ru.job4j.vacancy.JobExecutor;
import ru.job4j.vacancy.util.ExceptionUtil;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletContextEvent;
import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.concurrent.Executors;

import static org.slf4j.LoggerFactory.getLogger;

public class ContextConfigurator extends GuiceServletContextListener {
    private static final String JNDI_DATA_SOURCE = "java:/comp/env/jdbc/datasource";
    private static final String QUARTZ_PROPERTIES = "app.quartz.properties";

    private static final Logger log = getLogger(ContextConfigurator.class);

    private static final Injector injector = buildInjector();

    @Override
    protected Injector getInjector() {
        return injector;
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        log.info("Initialize Guice context");
        super.contextInitialized(servletContextEvent);

        JobExecutor executor = retrieveFromContext(JobExecutor.class);
        log.info("Start {}", executor.getClass().getSimpleName());
        // to prevent delay on an app start
        Executors.newSingleThreadExecutor().submit(() -> ExceptionUtil.handleRun(executor::start));
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        JobExecutor executor = retrieveFromContext(JobExecutor.class);
        log.info("Shutdown {}", executor.getClass().getSimpleName());
        ExceptionUtil.handleRun(() -> executor.shutdown(false));

        log.info("Destroy Guice context");
        super.contextDestroyed(servletContextEvent);
    }

    private static Injector buildInjector() {
        try {
            var dataSource = lookupDataSource();
            boolean isHsqldb = checkDataBase(dataSource);
            return Guice.createInjector(isHsqldb ? new DaoHsqldbModule(dataSource) : new DaoModule(dataSource),
                    new ExecutionModule(isHsqldb ? null : QUARTZ_PROPERTIES),
                    new ServiceModule(), new WebModule());
        } catch (Exception e) {
            throw new IllegalStateException("Cannot build injector context: " + e.getMessage(), e);
        }
    }

    private static boolean checkDataBase(DataSource dataSource) throws SQLException {
        try (var connection = dataSource.getConnection()) {
            return !"PostgreSQL".equals(connection.getMetaData().getDatabaseProductName());
        }
    }


    private static DataSource lookupDataSource() throws NamingException {
        var ctx = new InitialContext();
        return (DataSource) ctx.lookup(JNDI_DATA_SOURCE);
    }


    private static <T> T retrieveFromContext(Class<T> clazz) {
        return injector.getInstance(clazz);
    }
}
