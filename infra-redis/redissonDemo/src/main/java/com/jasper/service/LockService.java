package com.jasper.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

/**
 * @author jasper
 * @since 2026-08-31 <br>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LockService {

    private final RedissonClient redissonClient;

    public void execute(String lockName, long sleepMillis) {

        String lockKey = "lock:test:" + lockName;

        RLock lock = redissonClient.getLock(lockKey);

        lock.lock();

        try {

            log.info(
                    "[ENTER] thread={}, lockKey={}, time={}",
                    Thread.currentThread().getName(),
                    lockKey,
                    System.currentTimeMillis());

            Thread.sleep(sleepMillis);

            log.info(
                    "[EXIT ] thread={}, lockKey={}, time={}",
                    Thread.currentThread().getName(),
                    lockKey,
                    System.currentTimeMillis());

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            throw new RuntimeException(e);

        } finally {

            if (lock.isHeldByCurrentThread()) {

                lock.unlock();
            }
        }
    }
}
