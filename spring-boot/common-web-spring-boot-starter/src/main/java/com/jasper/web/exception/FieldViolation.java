package com.jasper.web.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Set;

/**
 * 单个字段的校验错误明细，结构与 validation-demo 中的 FieldViolation 保持一致。
 *
 * @param field         字段名，级联校验时形如 address.city
 * @param message       国际化后的错误文案
 * @param rejectedValue 被拒绝的原始值，敏感字段已由 security starter 自动脱敏
 * @param payload       约束携带的 payload，便于前端分级处理
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record FieldViolation(
        String field,
        String message,
        Object rejectedValue,
        Set<String> payload
) {

    public FieldViolation(String field, String message) {
        this(field, message, null, Set.of());
    }
}
