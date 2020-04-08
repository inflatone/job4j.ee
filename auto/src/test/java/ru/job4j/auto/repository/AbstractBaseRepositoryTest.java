package ru.job4j.auto.repository;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.job4j.auto.ActiveDbProfileResolver;
import ru.job4j.auto.SpringMvcTestConfig.SpringContextTestConfig;

@ActiveProfiles(resolver = ActiveDbProfileResolver.class)
@SpringJUnitConfig(classes = SpringContextTestConfig.class)
abstract class AbstractBaseRepositoryTest {
}
