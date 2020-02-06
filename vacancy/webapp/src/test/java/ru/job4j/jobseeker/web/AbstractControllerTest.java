package ru.job4j.jobseeker.web;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.servlet.GuiceFilter;
import name.falgout.jeffrey.testing.junit.guice.GuiceExtension;
import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import name.falgout.jeffrey.testing.junit.guice.IncludeModules;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.job4j.jobseeker.JdbiMockExtension;
import ru.job4j.jobseeker.inject.ServiceModule;
import ru.job4j.jobseeker.inject.StubDaoModule;
import ru.job4j.jobseeker.inject.StubExecutionModule;
import ru.job4j.jobseeker.inject.WebModule;
import ru.job4j.jobseeker.web.mock.WebMock;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.ServletException;

@ExtendWith(GuiceExtension.class) // https://github.com/JeffreyFalgout/junit5-extensions/tree/master/guice-extension
@ExtendWith(MockitoExtension.class)
@ExtendWith(JdbiMockExtension.class)
@IncludeModules({@IncludeModule(StubDaoModule.class), @IncludeModule(StubExecutionModule.class), @IncludeModule(ServiceModule.class), @IncludeModule(WebModule.class), @IncludeModule(AbstractControllerTest.WebMockModule.class)})
public class AbstractControllerTest {
    @Inject
    protected WebMock webMock;

    @BeforeEach
    void init() throws ServletException {
        webMock.setUp();
    }

    @AfterEach
    void terminate() {
        webMock.terminate();
    }

    public static class WebMockModule extends AbstractModule {
        @Provides
        @Singleton
        public Filter getGuiceFilter(Injector injector) {
            return injector.getInstance(GuiceFilter.class);
        }
    }

}
