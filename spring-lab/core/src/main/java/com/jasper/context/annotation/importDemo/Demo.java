package com.jasper.context.annotation.importDemo;

import com.jasper.service.DynamicService;
import com.jasper.service.ServiceA;
import com.jasper.service.ServiceB;
import com.jasper.service.ThirdPartyService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

@Slf4j
public class Demo {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(ImportConfig.class);
        // 刷新容器，触发 Bean 的解析和创建 (这一步不可或缺)
        context.refresh();
        // import class
        ThirdPartyService bean = context.getBean(ThirdPartyService.class);
        log.info(bean.toString());
        // importSelector
        ServiceA bean2 = context.getBean(ServiceA.class);
        log.info(bean2.toString());
        ServiceB bean3 = context.getBean(ServiceB.class);
        log.info(bean3.toString());
        DynamicService bean4 = context.getBean(DynamicService.class);
        log.info(bean4.toString());
        context.close();
    }
}
