package com.jasper.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.springframework.context.annotation.Configuration;

import jakarta.annotation.PostConstruct;

@Configuration
public class CircuitBreakerEventConfig {

    private final CircuitBreakerRegistry registry;

    public CircuitBreakerEventConfig(
            CircuitBreakerRegistry registry) {
        this.registry = registry;
    }

    @PostConstruct
    public void registerListeners() {
        registry.getAllCircuitBreakers().forEach(
            circuitBreaker ->
                circuitBreaker.getEventPublisher()
                    .onStateTransition(event ->
                        System.out.printf(
                            "CircuitBreaker=%s, transition=%s%n",
                            event.getCircuitBreakerName(),
                            event.getStateTransition()
                        )
                    )
        );
    }
}