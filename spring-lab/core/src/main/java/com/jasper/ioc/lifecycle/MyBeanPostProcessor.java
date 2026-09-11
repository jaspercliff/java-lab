package com.jasper.ioc.lifecycle;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * Spring 大量功能都是通过它扩展出来的
 */
@Component
public class MyBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(
            @NonNull Object bean,
            @NonNull String beanName) {

        if ("userService".equals(beanName)) {
            System.out.println(
                    "5 BeanPostProcessor.beforeInitialization"
            );
        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(
            @NonNull Object bean,
            @NonNull String beanName) {

        if ("userService".equals(beanName)) {
            System.out.println(
                    "7 BeanPostProcessor.afterInitialization"
            );
        }

        return bean;
    }
}