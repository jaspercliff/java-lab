package com.jasper.structural.proxy.dynamic.cglib;

import com.google.common.base.Stopwatch;
import com.jasper.structural.proxy.SmsResponse;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

@Slf4j
public class WatchInterceptor implements MethodInterceptor {
    @Override
    public Object intercept(Object o, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        Stopwatch stopwatch = Stopwatch.createStarted();
        // 1. 前置增强：参数检查
        String phone = (String) args[0];
        if (phone.startsWith("110")) {
            log.info(" [安全拦截] 敏感号码，禁止发送！");
            return new SmsResponse("REJECTED");
        }

        Object result = methodProxy.invokeSuper(o, args);
        stopwatch.stop();
        log.info(" [耗时] {} 毫秒", stopwatch.elapsed().toMillis());
        return result;
    }
}
