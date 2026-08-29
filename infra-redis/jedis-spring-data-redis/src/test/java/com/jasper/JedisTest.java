package com.jasper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import redis.clients.jedis.Jedis;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class JedisTest {

    Jedis jedis;

    @BeforeEach
    void setUp() {
        jedis = new Jedis("127.0.0.1", 6379);
        jedis.auth("passwd");
    }

    @AfterEach
    void tearDown() {
        jedis.close();
    }

    @Test
    public void testJedis() {

        jedis.set("name", "jasper");
        String value = jedis.get("name");
        assertEquals("jasper", value);
    }
}
