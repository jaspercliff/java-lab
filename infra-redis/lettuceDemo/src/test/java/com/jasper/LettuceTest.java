package com.jasper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * @author jasper
 * @since 2026-08-30 14:45:20 <br>
 */
@Testcontainers
public class LettuceTest {

    // static 是全局使用的 整个类
    @SuppressWarnings("resource") // 由testcontainer管理
    @Container
    static final GenericContainer<?> REDIS =
            new GenericContainer<>("redis:8")
                    .withCommand("redis-server", "--requirepass", "passwd")
                    .withExposedPorts(6379);

    String host;
    Integer port;
    StatefulRedisConnection<String, String> connection;
    RedisCommands<String, String> commands;
    RedisClient client;

    @BeforeEach
    void setUp() {
        host = REDIS.getHost();
        //        不依赖固定端口，避免测试端口冲突
        port = REDIS.getMappedPort(6379);
        RedisURI redisUri = RedisURI.Builder.redis(host, port).withPassword("passwd").build();
        client = RedisClient.create(redisUri);
        connection = client.connect();
        commands = connection.sync();
    }

    @AfterEach
    void teardown() {
        connection.close();
        client.close();
    }

    @Test
    void testSetAndGet() {

        commands.set("name", "jasper");
        String value = commands.get("name");
        assertEquals("jasper", value);
    }
}
