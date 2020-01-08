package ru.job4j.ee.store.web.inject;

import com.google.inject.AbstractModule;
import ru.job4j.ee.store.service.CityService;
import ru.job4j.ee.store.service.SecurityService;
import ru.job4j.ee.store.service.UserImageService;
import ru.job4j.ee.store.service.UserService;

public class ServiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(UserService.class).asEagerSingleton();
        bind(UserImageService.class).asEagerSingleton();
        bind(CityService.class).asEagerSingleton();
        bind(SecurityService.class).asEagerSingleton();
    }
}
