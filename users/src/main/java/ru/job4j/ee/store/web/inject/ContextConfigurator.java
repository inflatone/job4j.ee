package ru.job4j.ee.store.web.inject;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.servlet.GuiceServletContextListener;

public class ContextConfigurator extends GuiceServletContextListener {
    @Override
    protected Injector getInjector() {
        return Guice.createInjector(new JdbiRepositoryModule(), new ServiceModule(), new WebModule());
    }
}