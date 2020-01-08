package ru.job4j.ee.store.web.inject;

import com.google.inject.servlet.ServletModule;
import ru.job4j.ee.store.web.AdminServlet;
import ru.job4j.ee.store.web.AjaxServlet;
import ru.job4j.ee.store.web.ProfileServlet;
import ru.job4j.ee.store.web.UserImageServlet;
import ru.job4j.ee.store.web.auth.LoginServlet;
import ru.job4j.ee.store.web.auth.LogoutServlet;
import ru.job4j.ee.store.web.filters.AdminUserFilter;
import ru.job4j.ee.store.web.filters.CharsetFilter;
import ru.job4j.ee.store.web.filters.UnauthorizedUserFilter;

public class WebModule extends ServletModule {
    @Override
    protected void configureServlets() {
        configureControllers();

        filter("*").through(CharsetFilter.class);

        filter("*").through(UnauthorizedUserFilter.class);
        filter("/users").through(AdminUserFilter.class);

        serve("/login").with(LoginServlet.class);
        serve("/logout").with(LogoutServlet.class);

        serve("/users").with(AdminServlet.class);
        serve("/profile").with(ProfileServlet.class);

        serve("/images").with(UserImageServlet.class);

        serve("/ajax").with(AjaxServlet.class);
    }

    private void configureControllers() {
        bind(AdminServlet.class).asEagerSingleton();
        bind(ProfileServlet.class).asEagerSingleton();
        bind(UserImageServlet.class).asEagerSingleton();
        bind(AjaxServlet.class).asEagerSingleton();
        bind(LoginServlet.class).asEagerSingleton();
        bind(LogoutServlet.class).asEagerSingleton();

        bind(UnauthorizedUserFilter.class).asEagerSingleton();
        bind(AdminUserFilter.class).asEagerSingleton();
        bind(CharsetFilter.class).asEagerSingleton();
    }
}
