package com.jasper.circuitBreaker;

import lombok.Setter;

@Setter
class BackendService {
    private boolean shouldFail = false;

    public String doSomething() {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if (shouldFail) {
                throw new RuntimeException("Backend 挂了！");
            }
            return "正常响应数据";
        }
    }