package com.thread;


import com.jasper.thread.ThreadLocalManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.util.Map;

@Slf4j
public class ThreadLocalManagerTest {


    @Test
    public void test() {
        ThreadLocalManager.set("name","jasper");
        ThreadLocalManager.set("age",20);
        log.info("ThreadLocal Manager get: {}" ,ThreadLocalManager.get("name"));
        final boolean isContain = ThreadLocalManager.contains("name");
        log.info("isContain: {}", isContain);
        log.info("ThreadLocal Manager get: {}" ,ThreadLocalManager.get("age",Integer.class));
        ThreadLocalManager.remove("age");
        log.info("ThreadLocal Manager get after remove: {}" ,ThreadLocalManager.get("age"));

        final Map<String, Object> snapshot = ThreadLocalManager.snapshot();
        log.info("ThreadLocal Manager get snapshot: {}" ,snapshot);

        ThreadLocalManager.clear();
        log.info("ThreadLocal Manager get after clear: {}" ,ThreadLocalManager.get("name"));
        ThreadLocalManager.remove();
        log.info("ThreadLocal Manager get after clear: {}" ,ThreadLocalManager.snapshot());
    }
}
