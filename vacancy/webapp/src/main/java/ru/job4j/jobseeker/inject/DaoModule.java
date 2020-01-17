package ru.job4j.jobseeker.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import ru.job4j.jobseeker.dao.JdbiConfigurator;
import ru.job4j.jobseeker.dao.UserDao;

import javax.inject.Singleton;
import javax.naming.InitialContext;
import javax.sql.DataSource;

@Slf4j
public class DaoModule extends AbstractModule {
    public static final String JNDI_DATA_SOURCE = "java:/comp/env/jdbc/datasource";

    @Override
    public void configure() {
        bind(JdbiConfigurator.class).asEagerSingleton();
    }

    @Provides
    @Singleton
    protected DataSource provideDataSource() throws Exception {
        log.info("init JDBI with JNDI (from servlet container)");
        var ctx = new InitialContext();
        return (DataSource) ctx.lookup(JNDI_DATA_SOURCE);
    }

    @Provides
    @Singleton
    public UserDao provideUserDao(JdbiConfigurator configurator) {
        return configurator.getDao(UserDao.class);
    }
}
