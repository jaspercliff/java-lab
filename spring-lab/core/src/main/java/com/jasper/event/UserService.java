package com.jasper.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final ApplicationEventPublisher eventPublisher;
    @Transactional
    public void registerUser(String username, String email) {
        // 保存用户到数据库
        log.info("Registering user {} with email {}", username, email);
        // 发布事件（解耦后续的发邮件、送积分等操作）
        UserRegisterEvent event = new UserRegisterEvent(username, email);
        eventPublisher.publishEvent(event);
        // 模拟业务失败
        throw new RuntimeException("注册失败，模拟事务回滚");
    }
}