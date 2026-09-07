package com.jasper.proxy.jdk;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * InvocationHandler：调用处理器接口，代理对象的方法调用都会转发到该处理器的 invoke 方法中
 * Proxy：用于动态生成代理类和创建代理对象
 * 缺点：只能代理实现了接口的类
 */
@Slf4j
public class JdkProxyHandler implements InvocationHandler {
    private final Object target; // 目标对象

    public JdkProxyHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("【前置增强】方法执行前...");
        Object result = method.invoke(target, args); // 反射调用目标方法
        log.info("【后置增强】方法执行后...");
        return result;
    }
}