package com.jasper.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Configuration
// 指示是否创建基于子类（CGLIB）的代理，而不是标准的基于 Java 接口的代理 默认false
//springboot 默认true AopAutoConfiguration

// 指示代理对象是否应该被 AOP 框架暴露为一个 ThreadLocal，以便通过 AopContext 类进行检索。默认关闭 解决内部方法调用导致 AOP 失效
@EnableAspectJAutoProxy(proxyTargetClass = false, exposeProxy = true)
@ComponentScan("com.jasper")
public class AppConfig {
}
