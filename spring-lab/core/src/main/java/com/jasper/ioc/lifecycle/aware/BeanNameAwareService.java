package com.jasper.ioc.lifecycle.aware;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.stereotype.Component;

/**
 * 可以获取到当前 Bean 的名字
 * for 日志记录与监控
 */
@Component("myCustomService") // 这里的 "myCustomService" 就是 Bean 的名字
@Slf4j
public class BeanNameAwareService implements BeanNameAware {

    private String myBeanName;

    // Spring 会自动调用这个方法，把 Bean 的名字传进来
    @Override
    public void setBeanName(@NonNull String name) {
        this.myBeanName = name;
        log.info("beanName is :{}", this.myBeanName);
    }

    public void doSomething() {
        System.out.println("正在执行任务，当前 Bean 名称为: " + myBeanName);
    }
}