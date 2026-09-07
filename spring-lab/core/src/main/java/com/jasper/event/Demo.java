package com.jasper.event;

import com.jasper.event.generic.UserServiceA;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * 事件类需要继承 ApplicationEvent，监听器需要实现 ApplicationListener 接口。<br>
 * 但在现代 Spring Boot 中，推荐使用 POJO 作为事件，使用 @EventListener 注解作为监听器
 */
@Slf4j
public class Demo {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(EventAppConfig.class);
        UserService bean = context.getBean(UserService.class);
//        bean.registerUser("jasper", "1223@qq.com");
//        bean.registerUser("admin", "1223@qq.com");
        UserServiceA bean1 = context.getBean(UserServiceA.class);
        bean1.registerUser();
        context.close();
    }
}
