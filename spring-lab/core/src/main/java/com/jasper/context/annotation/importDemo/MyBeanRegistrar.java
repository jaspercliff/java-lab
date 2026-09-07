package com.jasper.context.annotation.importDemo;

import com.jasper.service.DynamicService;

import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author jasper
 * @since 2026-09-07 <br>
 */
public class MyBeanRegistrar implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(
            AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        // 动态创建一个 BeanDefinition
        RootBeanDefinition beanDefinition = new RootBeanDefinition(DynamicService.class);
        // 注册到容器中，指定 bean 的名称
        registry.registerBeanDefinition("dynamicService", beanDefinition);
    }
}
