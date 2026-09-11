package com.jasper.ioc.lifecycle;

import com.jasper.event.generic.User;
import com.jasper.ioc.lifecycle.aware.EnvironmentAwareComponent;
import com.jasper.ioc.lifecycle.aware.pay.PayStrategy;
import com.jasper.ioc.lifecycle.aware.pay.SmartPayStrategyFactory;
import com.jasper.ioc.lifecycle.beanPostProcessor.BeanPostProcessorComponent;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.math.BigDecimal;

@Slf4j
public class Demo {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(LifecycleConfig.class);
        UserService bean = context.getBean(UserService.class);
        bean.test();
        SmartPayStrategyFactory bean1 = context.getBean(SmartPayStrategyFactory.class);
        PayStrategy strategy = bean1.getStrategy("ALIPAY");
        strategy.pay(new BigDecimal("10"));
        EnvironmentAwareComponent bean2 = context.getBean(EnvironmentAwareComponent.class);
        String property = bean2.getProperty("java.version");
        log.info("property = {}", property);
        BeanPostProcessorComponent bean3 = context.getBean(BeanPostProcessorComponent.class);
        User userById = bean3.getUserById(1L);
        log.info("userById = {}", userById);
        context.close();
    }
}
