package com.jasper.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * 切点：定义哪些连接点需要被拦截
 */
@Slf4j
@Aspect
public class CommonPointcuts {

    @Pointcut("@annotation(com.jasper.anno.Loggable)")
    public void logPointcut() {
    }

    /**
     * public method
     * * any return type
     * com.jasper.controller   ..* package and sub-packages
     * .* any method name
     * (..) any parameters
     */
    @Pointcut("execution(public * com.jasper.service..*.*(..))")
    public void exePcd() {
    }

    @Pointcut("within(com.jasper.service..*)")
    public void withinPcd() {
    }

    /**
     * 这里是类上的
     */
    @Pointcut("@within(com.jasper.anno.ClassLoggable)")
    public void withinAnnoPcd() {
    }


    /**
     * args这可能会扫到 Spring 内部的 Bean、第三方库的 Bean，
     * 如果这些 Bean 有 final 方法，CGLib 就会报错
     * 给它加一个包路径限制
     */
    @Pointcut("inMyPackage() && args(String)")
    public void userParamPcd() {
    }

    @Pointcut("within(com.jasper..*)")
    public void inMyPackage() {
    }
}