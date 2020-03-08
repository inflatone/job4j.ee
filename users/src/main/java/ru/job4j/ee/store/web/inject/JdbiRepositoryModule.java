package ru.job4j.ee.store.web.inject;

import com.google.inject.AbstractModule;
import ru.job4j.ee.store.repository.*;
import ru.job4j.ee.store.repository.dbi.CityDao;
import ru.job4j.ee.store.repository.dbi.JdbiProvider;
import ru.job4j.ee.store.repository.dbi.UserDao;
import ru.job4j.ee.store.repository.dbi.UserImageDao;

public class JdbiRepositoryModule extends AbstractModule {
    @Override
    protected void configure() {
        // dao
        bind(UserDao.class).toInstance(JdbiProvider.getUserDao());
        bind(UserImageDao.class).toInstance(JdbiProvider.getUserImageDao());
        bind(CityDao.class).toInstance(JdbiProvider.getCityDao());

        // repository
        bind(UserRepository.class).to(JdbiUserRepository.class).asEagerSingleton();
        bind(UserImageRepository.class).to(JdbiUserImageRepository.class).asEagerSingleton();
        bind(CityRepository.class).to(JdbiCityRepository.class).asEagerSingleton();
    }
}