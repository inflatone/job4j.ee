package ru.job4j.jobseeker.inject;

import com.google.inject.servlet.ServletModule;
import ru.job4j.jobseeker.web.AdminController;
import ru.job4j.jobseeker.web.AjaxFormDataController;
import ru.job4j.jobseeker.web.ProfileController;
import ru.job4j.jobseeker.web.filter.CharsetFilter;

public class WebModule extends ServletModule {
    @Override
    protected void configureServlets() {
        configureClasses();

        filter("*").through(CharsetFilter.class);

        serve("/users").with(AdminController.class);
        serve("/profile").with(ProfileController.class);

        serve("/ajax").with(AjaxFormDataController.class);
    }

    private void configureClasses() {
        //servlets
        bind(AdminController.class).asEagerSingleton();
        bind(ProfileController.class).asEagerSingleton();

        bind(AjaxFormDataController.class).asEagerSingleton();

        //filters
        bind(CharsetFilter.class).asEagerSingleton();
    }
}
