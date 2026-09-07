package com.jasper.event.generic;


import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

public record EntityCreatedEvent<T>(
        T entity
) implements ResolvableTypeProvider {

    // 用于让事件对象主动告诉 Spring 我这个事件的完整类型是 EntityCreatedEvent<User>
    // for handle1
    @Override
    public ResolvableType getResolvableType() {
        return ResolvableType.forClassWithGenerics(
                EntityCreatedEvent.class,
                ResolvableType.forInstance(entity)
        );
    }
}