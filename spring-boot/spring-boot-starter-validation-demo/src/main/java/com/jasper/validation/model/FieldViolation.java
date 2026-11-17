package com.jasper.validation.model;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 单个字段的校验错误明细。 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldViolation {

    /** 字段名（级联时形如 address.city）。 */
    private String field;

    /** 国际化后的错误文案。 */
    private String message;

    /** 被拒绝的原始值（注意脱敏，示例里直接返回）。 */
    private Object rejectedValue;

    /** 约束携带的 payload（如 FatalPayload），便于前端分级处理。 */
    private Set<String> payload;
}
