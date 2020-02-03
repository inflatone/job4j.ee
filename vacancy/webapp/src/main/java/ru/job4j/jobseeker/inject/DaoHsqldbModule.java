package ru.job4j.jobseeker.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import ru.job4j.jobseeker.dao.*;

import javax.inject.Singleton;
import javax.sql.DataSource;

public class DaoHsqldbModule extends AbstractModule {
    public static final String SQL_RESOURCE_PATH = "hsqldb/";

    private final DataSource dataSource;

    public DaoHsqldbModule(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Provides
    @Singleton
    protected DataSource provideDataSource() {
        return dataSource;
    }

    @Provides
    @Singleton
    protected JdbiConfigurator provideJdbiConfigurator(DataSource dataSource) {
        return new JdbiConfigurator(dataSource)
                .buildUpDatabase(SQL_RESOURCE_PATH);
    }

    @Provides
    @Singleton
    public UserDao provideUserDao(JdbiConfigurator configurator) {
        return configurator.getDao(UserDao.class);
    }

    @Provides
    @Singleton
    public TaskDao provideTaskDao(JdbiConfigurator configurator) {
        return configurator.getDao(TaskDao.class);
    }

    @Provides
    @Singleton
    public VacancyDao provideVacancyDao(JdbiConfigurator configurator) {
        return configurator.getDao(VacancyHsqldbDao.class);
    }
}
