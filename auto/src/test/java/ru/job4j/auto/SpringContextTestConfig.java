package ru.job4j.auto;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;

@Configuration
@Import(EntityTestHelpers.class)
@ImportResource("classpath:/spring/spring-ctx.xml")
public class SpringContextTestConfig {
}
