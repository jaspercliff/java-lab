package com.jasper.enums;

import com.jasper.result.IResultCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 统一响应码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode implements IResultCode {

    // 成功
    SUCCESS(200, "操作成功"),

    BAD_REQUEST(400, "业务异常"),
    UNAUTHORIZED(401, "暂未登录或Token已经过期"),
    FORBIDDEN(403, "没有相关权限"),
    NOT_FOUND(404, "请求资源不存在"),
    METHOD_NOT_ALLOWED(405, "请求方法不允许"),
    VALIDATE_FAILED(412, "参数校验失败"),

    FAILED(500, "系统执行出错"),
    SERVICE_UNAVAILABLE(503, "服务不可用");

    private final Integer code;
    private final String msg;
}