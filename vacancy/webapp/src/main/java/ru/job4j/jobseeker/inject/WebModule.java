package ru.job4j.jobseeker.inject;

import com.google.inject.servlet.ServletModule;
import ru.job4j.jobseeker.web.filter.CharsetFilter;

public class WebModule extends ServletModule {
    @Override
    protected void configureServlets() {
        configureClasses();

        filter("*").through(CharsetFilter.class);

    }

    private void configureClasses() {
        //servlets

        //filters
        bind(CharsetFilter.class).asEagerSingleton();
    }
}
