package ru.job4j.ee.store.web.inject;

import com.google.inject.AbstractModule;
import ru.job4j.ee.store.repository.*;

public class InMemoryRepositoryModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(UserRepository.class).to(InMemoryUserRepository.class).asEagerSingleton();
        bind(UserImageRepository.class).to(InMemoryUserImageRepository.class).asEagerSingleton();
        bind(CityRepository.class).to(InMemoryCityRepository.class).asEagerSingleton();
    }
}