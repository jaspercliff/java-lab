package com.jasper.proxy.cglib;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * Enhancer：代理类生成器，用于设置父类和回调函数。
 * MethodInterceptor：方法拦截器，类似于JDK的 InvocationHandler。
 * 底层机制：通过ASM字节码技术，在内存中动态构建一个目标类的子类，并重写父类的方法。在重写的方法中加入拦截逻辑（类似AOP的增强）。
 */
@Slf4j
public class CglibInterceptor implements MethodInterceptor {
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
        log.info("【前置增强】订单创建前...");
        // 调用父类（目标类）的方法
        Object result = proxy.invokeSuper(obj, args); 
        log.info("【后置增强】订单创建后...");
        return result;
    }
}