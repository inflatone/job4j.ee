package ru.job4j.ee.store.service;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Module;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ru.job4j.ee.store.repository.CityRepository;
import ru.job4j.ee.store.repository.UserRepository;
import ru.job4j.ee.store.web.inject.ServiceModule;

import static ru.job4j.ee.store.AssertionUtil.cleanData;
import static ru.job4j.ee.store.AssertionUtil.fillData;

abstract class AbstractServiceTest {
    private final Module repositoryModule;

    @Inject
    protected UserRepository userRepository;

    @Inject
    protected CityRepository cityRepository;

    AbstractServiceTest(Module repositoryModule) {
        this.repositoryModule = repositoryModule;
    }

    @BeforeEach
    void init() {
        var injector = Guice.createInjector(repositoryModule, new ServiceModule());
        injector.injectMembers(this);

        fillData(userRepository, cityRepository);
    }

    @AfterEach
    void clean() {
        cleanData();
    }
}
