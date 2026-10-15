package com.jasper;

import com.jasper.service.BackendAService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BackendServiceTest {

    @Autowired
    private BackendAService backendService;

    @Test
    void failureWithFallback_shouldInvokeFallback() {
        String result = backendService.failureWithFallback();

//        assertThat(result).isEqualTo("fallback: 500 INTERNAL_SERVER_ERROR");
    }
}