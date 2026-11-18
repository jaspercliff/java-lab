package com.jasper.result;

/**
 * 响应码接口，方便自定义业务状态码枚举
 */
public interface IResultCode {
    /**
     * 获取状态码
     */
    Integer getCode();

    /**
     * 获取提示消息
     */
    String getMsg();

    /**
     * 系统错误码的国际化 key，默认 "SYS" + 数字码（如 SYS400）。
     *
     * <p>与业务错误码 {@link IBusinessResultCode} 的 RB 前缀分开，
     * 两类文案放在不同的 basename 中。
     */
    default String getI18nKey() {
        return "SYS" + getCode();
    }
}