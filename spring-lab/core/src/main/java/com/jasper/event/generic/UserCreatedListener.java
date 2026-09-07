package com.jasper.event.generic;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserCreatedListener {

    public UserCreatedListener() {
        log.info("UserCreatedListener 已创建");
    }
    @EventListener
    public void handle(EntityCreatedEvent<User> event) {
        log.info("进入泛型事件监听器");
        Object entity = event.entity();
        log.info("收到实体：{}", entity);
    }

    @EventListener
    public void handle1(EntityCreatedEvent<?> event) {
        log.info("进入泛型事件监听器");
        Object entity = event.entity();
        log.info("收到实体：{}", entity);
    }
}