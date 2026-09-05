package com.jasper.behavioral.observer;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class Subject {
    private final List<WeatherObserver> observers = new ArrayList<>();

    public AutoCloseable register(WeatherObserver observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
        // 返回一个匿名内部类或 Lambda，用于执行取消注册逻辑
        //实际开发中注销 防止list一直持有引用内存泄露
        return () -> {
            unregister(observer);
            assert observer != null;
            log.info("自动注销了:{} ", observer.getClass().getSimpleName());
        };
    }

    public void unregister(WeatherObserver observer) {
        observers.remove(observer);
    }

    // 父类直接实现好通知逻辑，并设为 final 防止子类乱改
    public final void notifyObservers(WeatherType weatherType) {
        for (WeatherObserver observer : observers) {
            observer.update(weatherType);
        }
    }
}
