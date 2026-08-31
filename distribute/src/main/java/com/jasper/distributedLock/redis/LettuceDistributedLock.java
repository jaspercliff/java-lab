package com.jasper.distributedLock.redis;

import io.lettuce.core.ScriptOutputType;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.sync.RedisCommands;

import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * @author jasper
 * @since 2026-08-30 <br>
 */
@Component
public class LettuceDistributedLock implements DistributedLock {

    private static final String UNLOCK_SCRIPT =
            """
            if redis.call('get', KEYS[1]) == ARGV[1] then
                return redis.call('del', KEYS[1])
            else
                return 0
            end
            """;

    private static final String RENEW_SCRIPT =
            """
            if redis.call('get', KEYS[1]) == ARGV[1] then
                return redis.call('pexpire', KEYS[1], ARGV[2])
            else
                return 0
            end
            """;

    private final RedisCommands<String, String> redis;

    public LettuceDistributedLock(RedisCommands<String, String> redis) {
        this.redis = redis;
    }

    /**
     * <br>
     * px ms <br>
     */
    @Override
    public boolean tryLock(String key, String requestId, Duration leaseTime) {

        String result = redis.set(key, requestId, SetArgs.Builder.nx().px(leaseTime.toMillis()));

        return "OK".equals(result);
    }

    @Override
    public boolean unlock(String key, String requestId) {

        Long result =
                redis.eval(UNLOCK_SCRIPT, ScriptOutputType.INTEGER, new String[] {key}, requestId);

        return Long.valueOf(1).equals(result);
    }

    @Override
    public boolean renew(String key, String requestId, Duration leaseTime) {

        Long result =
                redis.eval(
                        RENEW_SCRIPT,
                        ScriptOutputType.INTEGER,
                        new String[] {key},
                        requestId,
                        String.valueOf(leaseTime.toMillis()));

        return Long.valueOf(1).equals(result);
    }
}
