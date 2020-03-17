package ru.job4j.jobseeker.inject;

import com.google.inject.Provides;
import com.google.inject.servlet.ServletModule;
import com.google.inject.servlet.SessionScoped;
import ru.job4j.jobseeker.web.AdminController;
import ru.job4j.jobseeker.web.AjaxFormDataController;
import ru.job4j.jobseeker.web.ProfileController;
import ru.job4j.jobseeker.web.filter.CharsetFilter;
import ru.job4j.jobseeker.web.filter.NoAdminUserFilter;
import ru.job4j.jobseeker.web.filter.UnauthorizedUserFilter;
import ru.job4j.jobseeker.web.filter.UserRoleFilter;
import ru.job4j.jobseeker.web.security.AuthManager;
import ru.job4j.jobseeker.web.security.LoginController;
import ru.job4j.jobseeker.web.security.LogoutController;

public class WebModule extends ServletModule {
    @Override
    protected void configureServlets() {
        configureClasses();

        filter("*").through(CharsetFilter.class);
        filter("*").through(UserRoleFilter.class);

        filter("*").through(UnauthorizedUserFilter.class);
        filter("/users").through(NoAdminUserFilter.class);

        serve("/login").with(LoginController.class);
        serve("/logout").with(LogoutController.class);

        serve("/users").with(AdminController.class);
        serve("/profile").with(ProfileController.class);

        serve("/ajax").with(AjaxFormDataController.class);
    }

    private void configureClasses() {
        //servlets
        bind(AdminController.class).asEagerSingleton();
        bind(ProfileController.class).asEagerSingleton();

        bind(AjaxFormDataController.class).asEagerSingleton();

        bind(LoginController.class).asEagerSingleton();
        bind(LogoutController.class).asEagerSingleton();


        //filters
        bind(CharsetFilter.class).asEagerSingleton();

        bind(UserRoleFilter.class).asEagerSingleton();
        bind(UnauthorizedUserFilter.class).asEagerSingleton();
        bind(NoAdminUserFilter.class).asEagerSingleton();
    }

    @Provides
    @SessionScoped
    public AuthManager provideAuthManager() {
        return new AuthManager();
    }
}
