package ru.job4j.auto;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class ManualMain {
    public static void main(String[] args) {
        try (var context = new ClassPathXmlApplicationContext()) {
            context.getEnvironment().setActiveProfiles(ActiveDbProfileResolver.getActiveDbProfile());
            context.setConfigLocation("spring/spring-ctx.xml");
            context.refresh();
            printBeans(context);

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
