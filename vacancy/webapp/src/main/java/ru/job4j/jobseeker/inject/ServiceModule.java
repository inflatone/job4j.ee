package ru.job4j.jobseeker.inject;

import com.google.inject.AbstractModule;
import ru.job4j.jobseeker.service.SecurityService;
import ru.job4j.jobseeker.service.TaskService;
import ru.job4j.jobseeker.service.UserService;

public class ServiceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(UserService.class).asEagerSingleton();
        bind(SecurityService.class).asEagerSingleton();

        bind(TaskService.class).asEagerSingleton();
    }
}
