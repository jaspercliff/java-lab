package com.jasper;

import lombok.Setter;

@Setter
class BackendService {
    private boolean shouldFail = false;

    public String doSomething() {
            if (shouldFail) {
                throw new RuntimeException("Backend 挂了！");
            }
            return "正常响应数据";
        }
    }