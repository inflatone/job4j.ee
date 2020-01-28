package ru.job4j.jobseeker.inject;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;
import org.slf4j.Logger;
import ru.job4j.vacancy.JobExecutor;
import ru.job4j.vacancy.util.ExceptionUtil;

import javax.servlet.ServletContextEvent;
import java.util.concurrent.Executors;

import static org.slf4j.LoggerFactory.getLogger;

public class ContextConfigurator extends GuiceServletContextListener {
    private static final Logger log = getLogger(ContextConfigurator.class);

    private static final Injector injector = Guice.createInjector(
            new DaoModule(), new ExecutionModule(), new ServiceModule(), new WebModule());

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

    private static <T> T retrieveFromContext(Class<T> clazz) {
        return injector.getInstance(clazz);
    }
}
