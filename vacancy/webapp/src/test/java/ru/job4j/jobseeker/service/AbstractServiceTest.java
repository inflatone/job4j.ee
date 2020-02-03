package ru.job4j.jobseeker.service;

import name.falgout.jeffrey.testing.junit.guice.GuiceExtension;
import name.falgout.jeffrey.testing.junit.guice.IncludeModule;
import name.falgout.jeffrey.testing.junit.guice.IncludeModules;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.job4j.jobseeker.JdbiMockExtension;
import ru.job4j.jobseeker.inject.ServiceModule;
import ru.job4j.jobseeker.inject.StubDaoModule;
import ru.job4j.jobseeker.inject.StubExecutionModule;

import static ru.job4j.jobseeker.web.WebHelper.getRootCause;

@ExtendWith(GuiceExtension.class) // https://github.com/JeffreyFalgout/junit5-extensions/tree/master/guice-extension
@ExtendWith(MockitoExtension.class)
@ExtendWith(JdbiMockExtension.class)
@IncludeModules({@IncludeModule(StubDaoModule.class), @IncludeModule(ServiceModule.class), @IncludeModule(StubExecutionModule.class)})
public class AbstractServiceTest {
    public static <T extends Throwable> T assertRootThrows(Class<T> expectedType, Executable task) {
        return Assertions.assertThrows(expectedType, () -> {
            try {
                task.execute();
            } catch (Throwable t) {
                throw getRootCause(t);
            }
        });
    }
}
