package ru.job4j.ee.store.web;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import ru.job4j.ee.store.repository.CityRepository;
import ru.job4j.ee.store.repository.UserRepository;
import ru.job4j.ee.store.web.inject.InMemoryRepositoryModule;
import ru.job4j.ee.store.web.inject.ServiceModule;
import ru.job4j.ee.store.web.inject.WebModule;

import static ru.job4j.ee.store.AssertionUtil.cleanData;
import static ru.job4j.ee.store.AssertionUtil.fillData;

public class AbstractServletTest {
    protected Injector injector;

    @Inject
    protected UserRepository userRepository;

    @Inject
    protected CityRepository cityRepository;

    @BeforeEach
    void setUp() {
        injector = Guice.createInjector(new InMemoryRepositoryModule(), new ServiceModule(), new WebModule());
        injector.injectMembers(this);
        fillData(userRepository, cityRepository);
    }

    @AfterEach
    void clean() {
        cleanData();
    }
}
