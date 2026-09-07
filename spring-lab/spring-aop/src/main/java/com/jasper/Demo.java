package com.jasper;

import com.jasper.config.AppConfig;
import com.jasper.controller.UserController;
import com.jasper.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@Slf4j
public class Demo {

    public static void main(String[] args) {
//        一个可以读取 Java 配置类，并根据配置创建、管理 Bean 的 Spring 容器
//        Java 配置、注解配置
        ConfigurableApplicationContext context =
                new AnnotationConfigApplicationContext(AppConfig.class);
        UserService userService = context.getBean(UserService.class);
        userService.createUser("1");

        UserController userController = context.getBean(UserController.class);
        String user = userController.getUser();
        log.info("user >>> {}", user);
        context.close();
    }
}