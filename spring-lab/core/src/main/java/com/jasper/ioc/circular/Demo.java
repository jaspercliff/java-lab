package com.jasper.ioc.circular;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@Slf4j
public class Demo {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(AppConfig.class);
        AServiceCircle bean = context.getBean(AServiceCircle.class);
        log.info(bean.toString());
        context.close();
    }
}
