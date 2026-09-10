package com.jasper.aspect;

import com.jasper.anno.Loggable;
import com.jasper.controller.UserController;
import com.jasper.monitor.TimeTracker;
import com.jasper.service.UserService;
import com.jasper.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Slf4j
@Component // 一定要注册到spring中
@Aspect
public class LogAspect {

    @Around("com.jasper.aspect.CommonPointcuts.logPointcut()")
    public Object doAround(ProceedingJoinPoint joinPoint) {
        return TimeTracker.measure("log around aspect", () -> {
            MethodSignature signature =
                    (MethodSignature) joinPoint.getSignature();
            Method method = signature.getMethod();
            Loggable loggable = method.getAnnotation(Loggable.class);
            //  如果没找到（说明是 JDK 代理且注解写在实现类上），则去目标类的方法上找
            if (loggable == null) {
                Class<?> targetClass = joinPoint.getTarget().getClass();
                Method targetMethod = targetClass.getMethod(method.getName(), method.getParameterTypes());
                loggable = targetMethod.getAnnotation(Loggable.class);
            }
            log.info("loggable value: {}", loggable.value());
            log.info("request param is {}", joinPoint.getArgs());

            // 执行原来的业务方法，并返回业务结果
            try {
                return joinPoint.proceed();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Before("com.jasper.aspect.CommonPointcuts.exePcd()")
    public void doBefore(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        log.info("execution >>> AOP 验证成功！正在执行方法: {}", methodName);
    }

    @Before("com.jasper.aspect.CommonPointcuts.withinPcd()")
    public void doBeforeWithin(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        log.info("within >>> AOP 验证成功！正在执行方法: {}", methodName);
    }

    @Before("com.jasper.aspect.CommonPointcuts.withinAnnoPcd()")
    public void doBeforeWithinAnno(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        log.info("@within >>> AOP 验证成功！正在执行方法: {}", methodName);
    }

//     * @AfterReturning 方法正常返回后
//     * @AfterThrowing 方法抛出异常后
    @After("com.jasper.aspect.CommonPointcuts.userParamPcd()")
    public void doBeforeUserParam(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        log.info("args >>> AOP 验证成功！正在执行方法: {}", methodName);
    }

    //this 找的是proxy类 target找的原始类
//    CGLIB 生成的是子类，this 能拿到代理子类 这里使用jdk 则拿不到了
    @Before(value = "within(com.jasper.controller..*) && this(userControllerProxy)", argNames = "userControllerProxy")
    public void handleControllerThis(UserController userControllerProxy) {
        // 这里的 userControllerProxy 实际上是 UserController$$SpringCGLIB$$...
        // 因为它是子类，所以可以安全地强转/注入为 UserController
        log.info("CGLIB - this 捕获的对象类型: {}", userControllerProxy.getClass().getName());
    }

    @Before("within(com.jasper.controller..*) && target(userControllerTarget)")
    public void handleControllerTarget(UserController userControllerTarget) {
        // 这里的 userControllerTarget 是你写的那个原始类实例
        log.info("CGLIB - target 捕获的对象类型: {}", userControllerTarget.getClass().getName());
    }

    //  正确：this 只能声明为接口类型
    //  错误：this(serviceImpl) 永远不会匹配成功
    // 因为 JDK 代理对象并不是 UserServiceImpl 类型 但是是userService
//    args 解决 表达式里要找 userControllerProxy，但你方法里只提供了一个叫 arg0 的坑位，名字对不上，我没法赋值
    @Before(value = "within(com.jasper.service..*) && this(userServiceInterface)", argNames = "userServiceInterface")
    public void handleServiceThis(UserService userServiceInterface) {
        // 这里的对象是 $Proxy... 它实现了接口，所以能注入
        log.info("JDK - this (接口) 类型: {}", userServiceInterface.getClass().getName());
    }

    // 正确：target 可以声明为具体的实现类类型
    @Before("within(com.jasper.service..*) && target(userServiceImpl)")
    public void handleServiceTarget(UserServiceImpl userServiceImpl) {
        // 这里的对象是你写的原始实现类，所以能注入
        log.info("JDK - target (实现类) 类型: {}", userServiceImpl.getClass().getName());
    }

}
