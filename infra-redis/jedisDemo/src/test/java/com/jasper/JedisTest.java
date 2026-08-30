package com.jasper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import lombok.extern.slf4j.Slf4j;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.RedisClient;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Testcontainers
public class JedisTest {

    // static 是全局使用的 整个类
    @SuppressWarnings("resource") // 由testcontainer管理
    @Container
    static final GenericContainer<?> REDIS =
            new GenericContainer<>("redis:8")
                    .withCommand("redis-server", "--requirepass", "123456")
                    .withExposedPorts(6379);

    String host;
    Integer port;

    Jedis jedis;

    @BeforeEach
    void setUp() {
        host = REDIS.getHost();

        //        不依赖固定端口，避免测试端口冲突
        port = REDIS.getMappedPort(6379);
        jedis = new Jedis(host, port);
        jedis.auth("123456");
    }

    @AfterEach
    void tearDown() {
        jedis.close();
    }

    @Test
    void testSetAndGet() {

        jedis.set("name", "jasper");
        String value = jedis.get("name");
        assertEquals("jasper", value);
    }

    @Test
    void testThreadSafe() {
        new Thread(
                        () -> {
                            jedis.set("name", "jasper");
                        })
                .start();
        new Thread(
                        () -> {
                            jedis.set("name", "cliff");
                        })
                .start();
        String name = jedis.get("name");
        log.info("name={}", name); // ok 这里线程不安全
    }

    @Test
    void run() throws InterruptedException {

        // Jedis 7.2.0 introduced a new client connection API   Single connection (with connection
        // pooling)
        try (RedisClient jedis = RedisClient.create(host, port, null, "123456")) {

            int threadCount = 50;
            int operations = 1_000;

            ExecutorService executor = Executors.newFixedThreadPool(threadCount);
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch finishLatch = new CountDownLatch(threadCount);

            for (int i = 0; i < threadCount; i++) {
                int threadId = i;
                executor.submit(
                        () -> {
                            try {
                                // 让所有工作线程先停在这里，等待主线程，尽可能同时开始执行
                                startLatch.await();
                                for (int j = 0; j < operations; j++) {
                                    String key = "thread-safe:" + threadId;
                                    String value = threadId + "-" + j;
                                    jedis.set(key, value);
                                    String result = jedis.get(key);
                                    assertEquals(value, result);
                                }
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            } finally {
                                finishLatch.countDown();
                            }
                        });
            }
            startLatch.countDown();
            finishLatch.await();
            executor.shutdown();
        }
    }
}
