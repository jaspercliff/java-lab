package com.jasper;

import com.jasper.config.AppConfig;
import com.jasper.service.InnerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@Slf4j
public class ExposeDemo {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        InnerService bean = context.getBean(InnerService.class);
        bean.methodA();
        context.close();
    }
}
