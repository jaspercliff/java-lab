package com.jasper.circuitBreaker;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.event.CircuitBreakerEvent;
import io.github.resilience4j.consumer.CircularEventConsumer;
import io.github.resilience4j.core.EventConsumer;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;

@Slf4j
public class CircuitBreakerEventDemo {
    public static void main(String[] args) throws InterruptedException {
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(4)
                .minimumNumberOfCalls(4)
                .failureRateThreshold(50)
                .waitDurationInOpenState(Duration.ofMillis(3000)) // from open to half open
                //OpenState schedule  tryAcquirePermission manual
                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                .permittedNumberOfCallsInHalfOpenState(2)
//                .recordExceptions(IOException.class, TimeoutException.class)
//                .ignoreExceptions(BusinessException.class)
                .slowCallDurationThreshold(Duration.ofSeconds(1))
                .slowCallRateThreshold(60)
                .build();
        CircuitBreakerRegistry circuitBreakerRegistry = CircuitBreakerRegistry.of(circuitBreakerConfig);
        circuitBreakerRegistry.getEventPublisher()
                .onEntryAdded(entryAddedEvent -> {
                    CircuitBreaker addedCircuitBreaker = entryAddedEvent.getAddedEntry();
                    log.info("===CircuitBreaker {} added", addedCircuitBreaker.getName());
                })
                .onEntryRemoved(entryRemovedEvent -> {
                    CircuitBreaker removedCircuitBreaker = entryRemovedEvent.getRemovedEntry();
                    log.info("===CircuitBreaker {} removed", removedCircuitBreaker.getName());
                });

        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("backend");
        EventConsumer<CircuitBreakerEvent> consumer =
                event -> System.out.println("======收到事件：" + event);
        // 事件消费者缓冲区大小的参数 该消费者最多保留 10 个事件
        CircularEventConsumer<CircuitBreakerEvent> eventConsumer =
                new CircularEventConsumer<>(10);
        circuitBreaker.getEventPublisher()
                .onSuccess(event -> { // 当一次调用被熔断器记录为成功时产生
                    log.info("=====success event={}", event);
                })
                .onError(event -> { // 当一次调用的异常被熔断器记录为失败时产生。
                    log.info("=====error event={}", event);
                    log.warn(String.valueOf(event.getThrowable()));
                })
                .onIgnoredError(event -> { // 表示异常被配置为忽略，不计入正常的成功或失败统计
                    log.info("=====ignored event={}", event);
                })
                .onFailureRateExceeded(event -> {
                    log.info("======表示失败率已经达到或超过配置阈值 also minimum numbers of call, failure event={}", event);
                })
                .onSlowCallRateExceeded(event -> {
                    log.info("======表示慢调用率达到或超过阈值 event={}", event);
                })
                .onCallNotPermitted(event -> {
                    log.info("=======熔断器不允许请求继续执行 event={}", event);
                })
                .onStateTransition(state -> {
                    CircuitBreaker.StateTransition stateTransition = state.getStateTransition();
                    log.info("========状态转换 state event={}，before = {},after={}", state,stateTransition.getFromState()
                    ,stateTransition.getToState()
                    );
                })
                .onReset(event -> {
                    log.info("========reset event={}", event);
                })

//                .onEvent(consumer)
                .onEvent(eventConsumer);
        ;
//        eventConsumerBufferSize: 10
//        事件发布与消费：事件产生后交给注册的消费者。
//        事件缓冲区：将最近的一部分事件暂存起来，供后续读取
        ;
        BackendService backendService = new BackendService();
        for (int i = 1; i <= 6; i++) {

//            if (i==5){
//                Thread.sleep(3500); // make circuit breaker from open to half open
//            }
            // 让前 4 次请求故意失败，用来凑齐 minimumNumberOfCalls 并触发断路器开启
            // 第 5、6 次服务其实恢复了
            backendService.setShouldFail(i <= 4);

            try {
                // 使用 Resilience4j 的 executeSupplier 执行并受断路器保护
                String result = circuitBreaker.executeSupplier(backendService::doSomething);
                System.out.println("第 " + i + " 次调用成功: " + result);
            } catch (Exception e) {
                // 捕获异常（包括业务异常、或断路器开启时拒绝调用的 CallNotPermittedException）
                System.out.println("第 " + i + " 次调用异常/被拦截: [" + e.getClass().getSimpleName() + "] " + e.getMessage() + " -> 【走降级恢复逻辑】");
            }

            Thread.sleep(300); // 每次调用间隔 300 毫秒
        }
        // 查看缓冲区中保存的事件
        System.out.println("\n========== 缓冲区中的事件 ==========");

        eventConsumer.getBufferedEvents().forEach(event -> {
            log.info(event.toString());
        });
        circuitBreaker.reset();
    }
}
