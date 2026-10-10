package com.jasper.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditEventRepository auditEventRepository;
//    使用 Spring 的 ApplicationEventPublisher 发布审计事件
    private final ApplicationEventPublisher eventPublisher;


    @GetMapping("auditEventRepository")
    public String audit() {
        // 手动记录一条自定义审计事件
        AuditEvent event = new AuditEvent("jasper", "IMPORTANT_ACTION_SUCCESS",
                Map.of("detail", "用户成功执行了某项敏感操作"));
        auditEventRepository.add(event);
        return "audit success";
    }

    @GetMapping("applicationEventPublisher")
    public String audit1() {
        // 手动记录一条自定义审计事件
        AuditEvent event = new AuditEvent("cliff", "IMPORTANT_ACTION_SUCCESS",
                Map.of("detail", "用户成功执行了某项敏感操作"));
        auditEventRepository.add(event);
        return "audit success";
    }
}
