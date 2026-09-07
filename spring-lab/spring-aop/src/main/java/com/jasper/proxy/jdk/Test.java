package com.jasper.proxy.jdk;

import java.lang.reflect.Proxy;

public class Test {
    public static void main(String[] args) {
        System.setProperty("jdk.proxy.ProxyGenerator.saveGeneratedFiles", "true");
        UserService target = new UserServiceImpl();
        // 生成代理对象
        UserService proxyInstance = (UserService) Proxy.newProxyInstance(
                target.getClass().getClassLoader(),
                target.getClass().getInterfaces(),
                new JdkProxyHandler(target)
        );
        
        proxyInstance.addUser();
    }
}