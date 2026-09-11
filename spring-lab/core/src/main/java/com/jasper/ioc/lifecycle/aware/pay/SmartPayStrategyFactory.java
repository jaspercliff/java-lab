package com.jasper.ioc.lifecycle.aware.pay;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SmartPayStrategyFactory implements ApplicationContextAware, SmartInitializingSingleton {

    private ApplicationContext applicationContext;
    
    private final Map<String, PayStrategy> strategyMap = new ConcurrentHashMap<>();

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    // 在所有单例 Bean 都初始化完成后执行
    @Override
    public void afterSingletonsInstantiated() {
        // 此时 100% 安全，所有策略 Bean 都已经是个完全体
        Map<String, PayStrategy> beans = applicationContext.getBeansOfType(PayStrategy.class);
        beans.forEach((k, v) -> strategyMap.put(v.getType(), v));
    }

    public PayStrategy getStrategy(String type) {
        PayStrategy strategy = strategyMap.get(type);
        if (strategy == null) {
            throw new IllegalArgumentException("不支持的支付类型: " + type);
        }
        return strategy;
    }
}