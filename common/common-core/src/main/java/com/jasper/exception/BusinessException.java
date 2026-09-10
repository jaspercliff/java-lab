package com.jasper.exception;

import lombok.Getter;

/**
 * 业务异常
 *
 * @author jasper
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 业务错误码
     */
    private final String code;

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }

    public BusinessException(String message) {
        this("BUSINESS_ERROR", message);
    }

    public BusinessException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }
}