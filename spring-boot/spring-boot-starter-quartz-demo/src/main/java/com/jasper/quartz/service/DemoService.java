package com.jasper.quartz.service;

import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

/**
 * 普通 Spring Service，用来证明 Quartz Job 可以正常使用容器内的 Bean。
 *
 * <p>真实项目里这里可能是订单服务、报表服务、消息发送器等。
 * 注意：Job 中调用外部服务时要考虑幂等、超时和异常处理，Quartz 只负责“按时触发”，不负责业务可靠性。
 */
@Service
public class DemoService {

    private final AtomicLong counter = new AtomicLong();

    /**
     * 模拟一次业务调用。
     *
     * @param jobKey 触发来源，便于日志追踪
     * @return 本次调用结果
     */
    public String work(String jobKey) {
        long value = counter.incrementAndGet();
        return "DemoService.processed(" + jobKey + ")#" + value;
    }
}
