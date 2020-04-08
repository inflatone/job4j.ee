package ru.job4j.auto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static ru.job4j.auto.TestModelData.USER;


public class ManualMain {
    public static void main(String[] args) throws JsonProcessingException {
        try (var context = new ClassPathXmlApplicationContext()) {
            context.getEnvironment().setActiveProfiles(ActiveDbProfileResolver.getActiveDbProfile());
            context.setConfigLocations("spring/spring-ctx.xml", "spring/spring-mvc.xml");
            context.refresh();
            printBeans(context);
            System.out.println();
            System.out.println();

            ObjectMapper bean = context.getBean(ObjectMapper.class);
            System.out.println(bean.writeValueAsString(USER));
        }
    }

    public static void printBeans(ConfigurableApplicationContext springContext) {
        System.out.println("\nBean definition names: ");
        for (String beanName : springContext.getBeanDefinitionNames()) {
            System.out.println(' ' + beanName);
        }
        System.out.println();
    }
}
