package com.jasper.ioc.circular;

import com.jasper.context.annotation.importDemo.ImportConfig;
import com.jasper.service.DynamicService;
import com.jasper.service.ServiceA;
import com.jasper.service.ServiceB;
import com.jasper.service.ThirdPartyService;

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
