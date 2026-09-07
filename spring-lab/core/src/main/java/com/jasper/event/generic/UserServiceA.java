package com.jasper.event.generic;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserServiceA {

    private final ApplicationEventPublisher eventPublisher;

    public void registerUser() {
        User user = new User(1L, "jasper");

        eventPublisher.publishEvent(
                new EntityCreatedEvent<User>(user)
        );
    }
}