package com.jasper.web.advice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jasper.result.ApiResult;
import com.jasper.web.annotation.IgnoreApiResult;
import com.jasper.web.config.properties.ResponseProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

/**
 * 把 Controller 的返回值统一包装成 {@link ApiResult#success(Object)}。
 *
 * <p>跳过的场景：已经是 ApiResult、ResponseEntity、文件/流资源、SSE、{@code void}、
 * Spring Boot 的 /error 响应、非 {@code @ResponseBody} 控制器，以及标注了
 * {@link IgnoreApiResult} 的类或方法。
 *
 * <p>String 返回值的转换器是 StringHttpMessageConverter，直接返回对象会变成 toString()，
 * 因此这里显式序列化成 JSON 字符串再交回给该转换器。
 */
@Slf4j
@RequiredArgsConstructor
@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class ApiResultResponseAdvice implements ResponseBodyAdvice<Object> {

    private final ObjectProvider<ObjectMapper> objectMapper;
    private final ResponseProperties properties;

    /**
     * 决定哪些请求需要包装
     */
    @Override
    public boolean supports(@NonNull MethodParameter returnType,
                            @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        if (!properties.enabled()) {
            return false;
        }
        // 只对 JSON 和 String 转换器生效，避免影响文件下载等其它转换器
        /* {@link https://docs.spring.io/spring-framework/reference/6.2/web/webmvc/message-converters.html} */
        if (!AbstractJackson2HttpMessageConverter.class.isAssignableFrom(converterType)
                && !StringHttpMessageConverter.class.isAssignableFrom(converterType)) {
            return false;
        }

        Class<?> type = returnType.getParameterType();
        if (type == void.class || type == Void.class
                || ApiResult.class.isAssignableFrom(type)
                || ResponseEntity.class.isAssignableFrom(type)
                // 文件下载
                || Resource.class.isAssignableFrom(type)
                // 服务器发送事件 客户端发起一次 HTTP 请求后，不需要一直重复请求，服务器可以持续向客户端发送消息
                || SseEmitter.class.isAssignableFrom(type)
                // 流式生成数据
                || StreamingResponseBody.class.isAssignableFrom(type)) {
            return false;
        }

        // Spring Boot 有一个默认的错误控制器 /error
        Class<?> declaringClass = returnType.getDeclaringClass();
        if (BasicErrorController.class.isAssignableFrom(declaringClass)) {
            return false;
        }
        Class<?> containingClass = returnType.getContainingClass();
        if (AnnotatedElementUtils.hasAnnotation(containingClass, IgnoreApiResult.class)
                || returnType.hasMethodAnnotation(IgnoreApiResult.class)) {
            return false;
        }
        return returnType.hasMethodAnnotation(ResponseBody.class)
                || AnnotatedElementUtils.hasAnnotation(
                containingClass,
                ResponseBody.class
        );
    }

    @Override
    public Object beforeBodyWrite(Object body, @NonNull MethodParameter returnType,
                                  @NonNull MediaType selectedContentType,
                                  @NonNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  @NonNull ServerHttpRequest request,
                                  @NonNull ServerHttpResponse response) {
        if (body instanceof ApiResult<?>) {
            return body;
        }
        ApiResult<Object> wrapped = ApiResult.success(body);
        // String 转换器无法正常将这个对象转换为 JSON 所以下面手动处理
        if (!StringHttpMessageConverter.class.isAssignableFrom(selectedConverterType)) {
            return wrapped;
        }

        ObjectMapper mapper = objectMapper.getIfAvailable();
        if (mapper == null) {
            return body;
        }
        try {
            // 不在这里改 response 的 Content-Type：selectedContentType 由 MVC 根据 converter 决定，
            // beforeBodyWrite 之后才真正写出 body，此处强改 header 属于反模式且对 null response 不友好。
            return mapper.writeValueAsString(wrapped);
        } catch (JsonProcessingException ex) {
            log.warn("包装 String 响应失败，返回原始响应: {}", ex.getMessage());
            return body;
        }
    }
}
