package com.jasper.proxy.cglib;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.core.DebuggingClassWriter;
import org.springframework.cglib.proxy.Enhancer;

import java.util.Scanner;

@Slf4j
public class TestCglib {
    public static void main(String[] args) {
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY,"/Users/jasper/code/java/tmp/cglibDump");
        Enhancer enhancer = new Enhancer();
        // 设置父类（目标类）
        enhancer.setSuperclass(OrderService.class);
        // 设置回调拦截器
        enhancer.setCallback(new CglibInterceptor());
        
        // 生成代理对象（实际上是OrderService的子类）
        OrderService proxy = (OrderService) enhancer.create();
        proxy.createOrder();

        new Scanner(System.in).nextLine(); // 等待用户按回车键退出
        log.info("exit");
    }
}