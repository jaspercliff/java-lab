package com.jasper.proxy.jdk;

import com.jasper.proxy.UserService;
import com.jasper.proxy.UserServiceImpl;

import java.lang.reflect.Proxy;

public class Test {
    public static void main(String[] args) {
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