package com.jasper.validation.constraint;

import jakarta.validation.Payload;

/**
 * 知识点：自定义 {@link Payload} 标记。
 *
 * <p>Payload 本身不参与校验，是挂在约束上的「元数据」。全局异常处理器可用
 * {@code constraintViolation.getConstraintDescriptor().getPayload()} 判断错误级别，
 * 例如带 FatalPayload 的字段错误返回特定错误码。
 */
public interface FatalPayload extends Payload {
}
