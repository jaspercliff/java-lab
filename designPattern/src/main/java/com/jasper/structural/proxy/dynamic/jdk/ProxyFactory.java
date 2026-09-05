package com.jasper.structural.proxy.dynamic.jdk;

import java.lang.reflect.Proxy;

public class ProxyFactory {
    public static Object getProxy(Object target) {
        return Proxy.newProxyInstance(
                target.getClass().getClassLoader(), // 目标类的类加载器  TxSmsService
                target.getClass().getInterfaces(),  // 代理需要实现的接口，可指定多个 SmsService
                new WatchInvocationHandler(target)   // 代理对象对应的自定义 InvocationHandler
        );
    }
}