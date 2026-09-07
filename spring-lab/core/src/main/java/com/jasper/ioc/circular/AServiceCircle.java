package com.jasper.ioc.circular;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class AServiceCircle {
    public final BServiceCircle bServiceCircle;

    /**
     * 使用字段注入 spring.main.allow-circular-references = true
     * spring 默认允许 AbstractAutowireCapableBeanFactory 写死的
     * 但是springboot 2.6以后默认为false了
     * 使用@lazy 在其中一个构造参数上加上 @Lazy。它会告诉spring：“你先给我弄个假代理占个位， 等我真正用到这个对象时，你再去容器里找,相当于绕开循环依赖
     */
    @Autowired // 构造器注入
    @Lazy //懒加载  Spring 并没有立刻去实例化真正的 BService，而是利用 CGLIB 或 JDK 动态代理生成一个 BService 的代理占位符
    public AServiceCircle(BServiceCircle bServiceCircle) {
        this.bServiceCircle = bServiceCircle;
    }
}
