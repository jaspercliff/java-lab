package com.jasper.ioc.lifecycle.beanPostProcessor;

import com.jasper.event.generic.User;
import com.jasper.monitor.TimeTracker;
import org.springframework.stereotype.Service;

@Service
public class BeanPostProcessorComponent {

    @CostTime("获取用户详情")
    public User getUserById(Long id) {
        // 模拟嵌套调用
        TimeTracker.measure("查询数据库", () -> {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignored) {
            }
        });

        if (id == 0) {
            throw new RuntimeException("用户不存在"); // 测试异常捕获
        }
        return new User(id, "Jasper");
    }
}