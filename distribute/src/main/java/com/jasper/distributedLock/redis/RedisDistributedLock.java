package com.jasper.distributedLock.redis;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;

/**
 * @author jasper
 * @since 2026-08-30 <br>
 */
@Component
public class RedisDistributedLock implements DistributedLock {

    private static final DefaultRedisScript<Long> UNLOCK_SCRIPT =
            new DefaultRedisScript<>(
                    """
                    if redis.call('get', KEYS[1]) == ARGV[1] then
                        return redis.call('del', KEYS[1])
                    else
                        return 0
                    end
                    """,
                    Long.class);

    private static final DefaultRedisScript<Long> RENEW_SCRIPT =
            new DefaultRedisScript<>(
                    """
                    if redis.call('get', KEYS[1]) == ARGV[1] then
                        return redis.call('pexpire', KEYS[1], ARGV[2])
                    else
                        return 0
                    end
                    """,
                    Long.class);

    @Qualifier("redisTemplate")
    private final RedisTemplate<String, String> redisTemplate;

    public RedisDistributedLock(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Override
    public boolean tryLock(String key, String requestId, Duration leaseTime) {

        Boolean result = redisTemplate.opsForValue().setIfAbsent(key, requestId, leaseTime);

        return Boolean.TRUE.equals(result);
    }

    @Override
    public boolean unlock(String key, String requestId) {

        Long result =
                redisTemplate.execute(UNLOCK_SCRIPT, Collections.singletonList(key), requestId);

        return Long.valueOf(1).equals(result);
    }

    @Override
    public boolean renew(String key, String requestId, Duration leaseTime) {

        Long result =
                redisTemplate.execute(
                        RENEW_SCRIPT,
                        Collections.singletonList(key),
                        requestId,
                        String.valueOf(leaseTime.toMillis()));

        return Long.valueOf(1).equals(result);
    }
}
