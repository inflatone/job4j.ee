package ru.job4j.jobseeker.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import ru.job4j.jobseeker.JdbiConfiguratorHolder;
import ru.job4j.jobseeker.dao.*;

import javax.inject.Singleton;

public class StubDaoModule extends AbstractModule {
    @Provides
    @Singleton
    public JdbiConfigurator provideJdbiConfigurator() {
        return JdbiConfiguratorHolder.getJdbiConfigurator();
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
