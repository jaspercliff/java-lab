package com.jasper.structural.proxy.dynamic.jdk;

import com.google.common.base.Stopwatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Slf4j
public class WatchInvocationHandler implements InvocationHandler {

    private final Object target;

    @Override
    public Object invoke(Object o, Method method, Object[] args) throws Throwable {

        String phone = (String)args[0];
        if ("110".equals(phone)){
            log.info("phone number is 110, don't");
        }
        Stopwatch stopwatch = Stopwatch.createStarted();
        Object result = method.invoke(target, args);
        stopwatch.stop();
        log.info("cost time: {} ms",stopwatch.elapsed(TimeUnit.MILLISECONDS));
        return result;
    }
}
