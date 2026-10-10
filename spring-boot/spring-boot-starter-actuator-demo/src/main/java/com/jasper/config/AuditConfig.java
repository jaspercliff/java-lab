package com.jasper.config;

import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.boot.actuate.audit.InMemoryAuditEventRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 审计日志主要用于安全合规、追查责任和风险监控
 * eg:系统必须记录用户的关键敏感操作，或者内部员工发生违规操作（如删除了核心数据、窃取了敏感信息）
 */
@Configuration
public class AuditConfig {

    @Bean
    public AuditEventRepository auditEventRepository() {
        // 使用 Spring 官方自带的内存型审计事件仓库
        // 也可以实现 AuditEventRepository 接口，将事件持久化到 MySQL、MongoDB 等数据库中
        // 除了依靠 Spring Security 自动记录登录/登出等事件外，
        // 也可以在业务代码中注入 AuditEventRepository（或者使用 Spring 的 ApplicationEventPublisher 发布事件）
        return new InMemoryAuditEventRepository();
    }
}