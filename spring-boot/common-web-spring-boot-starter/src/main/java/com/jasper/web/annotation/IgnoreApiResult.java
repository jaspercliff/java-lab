package com.jasper.web.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记在 Controller 类或方法上，跳过 ApiResult 成功响应自动包装。
 *
 * <p>适用于文件下载、SSE、自定义响应体等不适合被包装的接口。
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface IgnoreApiResult {
}
