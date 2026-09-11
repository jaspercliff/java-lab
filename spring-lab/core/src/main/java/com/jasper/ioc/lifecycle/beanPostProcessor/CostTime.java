package com.jasper.ioc.lifecycle.beanPostProcessor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CostTime {
    // 允许自定义监控名称，例如 @CostTime("查询用户信息")
    String value() default ""; 
}