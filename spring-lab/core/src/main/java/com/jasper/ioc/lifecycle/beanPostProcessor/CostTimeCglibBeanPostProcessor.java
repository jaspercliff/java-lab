package com.jasper.ioc.lifecycle.beanPostProcessor;

import com.jasper.monitor.TimeTracker;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;

@Component
public class CostTimeCglibBeanPostProcessor implements BeanPostProcessor {

    private static @NonNull Enhancer createEnhancer(Object bean) {
        Enhancer enhancer = new Enhancer();

        // 设置父类为目标 Bean 的 Class
        enhancer.setSuperclass(bean.getClass());

        // 设置回调拦截器
        enhancer.setCallback((MethodInterceptor) (obj, method, args, methodProxy) -> {
            // 4. 判断当前执行的方法是否带有 @CostTime 注解
            if (method.isAnnotationPresent(CostTime.class)) {
                CostTime costTime = method.getAnnotation(CostTime.class);

                // 确定 Tracker 的名称
                String trackerName = StringUtils.hasText(costTime.value())
                        ? costTime.value()
                        : bean.getClass().getSimpleName() + "." + method.getName();

                // 注意：这里必须使用 methodProxy.invokeSuper，而不是 method.invoke
                return TimeTracker.measure(trackerName, () -> {
                    try {
                        return methodProxy.invokeSuper(obj, args);
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                });
            }

            // 没有注解的方法，正常执行，不增加任何开销
            return methodProxy.invokeSuper(obj, args);
        });
        return enhancer;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        // 1. 性能优化：只拦截带有 @Service 或 @RestController 的 Bean
//        if (!bean.getClass().isAnnotationPresent(Service.class) &&
//                !bean.getClass().isAnnotationPresent(org.springframework.web.bind.annotation.RestController.class)) {
//            return bean;
//        }

        // 2. 快速检查：该 Bean 中是否有任何方法被 @CostTime 标注
        boolean hasAnnotation = Arrays.stream(bean.getClass().getDeclaredMethods())
                .anyMatch(m -> m.isAnnotationPresent(CostTime.class));

        if (hasAnnotation) {
            // 3. 核心：使用 CGLIB Enhancer 生成代理对象
            Enhancer enhancer = createEnhancer(bean);

            // 6. 创建并返回代理对象，替换 Spring 容器中原来的 Bean
            return enhancer.create();
        }

        return bean;
    }
}