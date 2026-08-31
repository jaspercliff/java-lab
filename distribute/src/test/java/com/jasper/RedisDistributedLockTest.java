package com.jasper;

import com.jasper.distributedLock.redis.LettuceDistributedLock;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author jasper
 * @since 2026-08-31 <br>
 */
@SpringBootTest
class RedisDistributedLockTest {

    @Autowired private LettuceDistributedLock lock;

    @Test
    void testConcurrentLock() throws InterruptedException {

        String lockKey = "lock:test";
        int threadCount = 10;
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        for (int i = 0; i < threadCount; i++) {
            int threadId = i;
            executor.submit(
                    () -> {
                        String requestId = UUID.randomUUID().toString();
                        try {
                            // 所有线程同时开始
                            start.await();
                            boolean locked =
                                    lock.tryLock(lockKey, requestId, Duration.ofSeconds(10));
                            if (!locked) {
                                System.out.println("线程 " + threadId + " 获取锁失败");
                                return;
                            }
                            try {
                                System.out.println("线程 " + threadId + " 获取锁成功");
                                // 模拟业务
                                Thread.sleep(2000);
                                System.out.println("线程 " + threadId + " 业务执行完成");
                            } finally {
                                lock.unlock(lockKey, requestId);
                                System.out.println("线程 " + threadId + " 释放锁");
                            }
                        } catch (Exception e) {
                            Thread.currentThread().interrupt();
                            throw new RuntimeException(e);
                        } finally {
                            done.countDown();
                        }
                    });
        }

        // 放行所有线程
        start.countDown();

        // 等待所有线程执行完
        done.await();

        executor.shutdown();
    }
}
