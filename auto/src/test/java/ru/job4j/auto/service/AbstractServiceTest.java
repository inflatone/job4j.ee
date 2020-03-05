package ru.job4j.auto.service;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import ru.job4j.auto.ActiveDbProfileResolver;
import ru.job4j.auto.EntityTestHelpers;
import ru.job4j.auto.SpringMvcTestConfig.SpringContextTestConfig;

@ActiveProfiles(resolver = ActiveDbProfileResolver.class)
@SpringJUnitConfig(classes = SpringContextTestConfig.class)
@Sql(scripts = {"classpath:db/import/data.sql"}, config = @SqlConfig(encoding = "UTF-8"))
abstract class AbstractServiceTest extends EntityTestHelpers {
}
