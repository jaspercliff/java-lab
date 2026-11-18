package com.jasper.web.exception;

import com.jasper.exception.BusinessException;
import com.jasper.result.ApiResult;
import com.jasper.result.ResultCode;
import com.jasper.security.core.SensitiveFieldMatcher;
import com.jasper.web.config.properties.WebProperties;
import com.jasper.web.constants.WebResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 全局异常处理：把散落在各业务模块的异常处理收敛到 starter，统一返回 common 的 {@link ApiResult}。
 *
 * <p>所有错误文案都经 {@link MessageSource} 按当前请求 Locale 解析：
 * 业务码（RB1001）走 business basename，系统码（SYS400 / VALIDATION_*）走 system basename。
 * 找不到 key 时用方法传入的默认文案兜底。
 *
 * <p>校验异常中的 rejectedValue 统一经 {@link SensitiveFieldMatcher} 脱敏，
 * 密码、token 等敏感字段不会回显到前端。
 *
 * <p>使用者若需要定制，可自定义 {@code GlobalExceptionHandler} 类型的 Bean 覆盖本实现；
 * 或定义自己的 {@code @RestControllerAdvice} 并使用更高优先级（如 {@code @Order(0)}）。
 */
@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class GlobalExceptionHandler {

    private static final String CODE_BAD_REQUEST = String.valueOf(ResultCode.BAD_REQUEST.getCode());
    private static final String CODE_NOT_FOUND = String.valueOf(ResultCode.NOT_FOUND.getCode());
    private static final String CODE_METHOD_NOT_ALLOWED = String.valueOf(ResultCode.METHOD_NOT_ALLOWED.getCode());
    private static final String CODE_INTERNAL_ERROR = String.valueOf(ResultCode.INTERNAL_SERVER_ERROR.getCode());

    private final WebProperties properties;
    private final SensitiveFieldMatcher sensitiveMatcher;
    private final MessageSource messageSource;

    /**
     * 业务异常：ApiResult.code 直接取业务码（RB1001）或系统码字符串，message 按 i18n key 解析。
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<BusinessError> handleBusiness(BusinessException ex, HttpServletRequest request) {
        log.warn("业务异常 [{}] {}: {}", ex.getCode(), request.getRequestURI(), ex.getMessage());
        String message = message(ex.getI18nKey(), ex.getMessage());
        return ApiResult.error(ex.getCode(), message, new BusinessError(ex.getCode()));
    }

    /** ① {@code @Valid @RequestBody} 失败：BindingResult 中字段错误最全。 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<List<FieldViolation>> handleBody(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<FieldViolation> violations = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(fe -> violations.add(new FieldViolation(
                fe.getField(),
                fe.getDefaultMessage(),
                sensitiveMatcher.mask(fe.getField(), fe.getRejectedValue()),
                Set.of())));
        ex.getBindingResult().getGlobalErrors().forEach(oe -> violations.add(new FieldViolation(
                oe.getObjectName(), oe.getDefaultMessage())));
        log.warn("参数校验失败 [VALIDATION_BODY] {}: {}", request.getRequestURI(), violations);
        return ApiResult.error(CODE_BAD_REQUEST,
                message("VALIDATION_BODY", "请求参数校验失败"), violations);
    }

    /** ② Spring 6.1+ 方法参数上的约束（Controller 类上没有 {@code @Validated} 时）。 */
    @ExceptionHandler(HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<List<FieldViolation>> handleMethodParams(HandlerMethodValidationException ex,
                                                              HttpServletRequest request) {
        List<FieldViolation> violations = ex.getParameterValidationResults().stream()
                .flatMap(result -> {
                    String field = result.getMethodParameter().getParameterName();
                    Object masked = sensitiveMatcher.mask(field, result.getArgument());
                    return result.getResolvableErrors().stream()
                            .map(err -> new FieldViolation(field, err.getDefaultMessage(), masked, Set.of()));
                })
                .toList();
        log.warn("参数校验失败 [VALIDATION_PARAM] {}: {}", request.getRequestURI(), violations);
        return ApiResult.error(CODE_BAD_REQUEST,
                message("VALIDATION_PARAM", "方法参数校验失败"), violations);
    }

    /** ③ Service 方法级 {@code @Validated} 失败：可从 ConstraintViolation 拿到 payload。 */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<List<FieldViolation>> handleConstraint(ConstraintViolationException ex,
                                                            HttpServletRequest request) {
        List<FieldViolation> violations = ex.getConstraintViolations().stream()
                .map(this::toViolation)
                .toList();
        log.warn("参数校验失败 [VALIDATION_METHOD] {}: {}", request.getRequestURI(), violations);
        return ApiResult.error(CODE_BAD_REQUEST,
                message("VALIDATION_METHOD", "方法入参校验失败"), violations);
    }

    /** 请求体 JSON 格式错误 / 类型无法转换。 */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<Void> handleUnreadable(HttpMessageNotReadableException ex, HttpServletRequest request) {
        log.warn("请求体解析失败 {}: {}", request.getRequestURI(), ex.getMessage());
        return ApiResult.error(CODE_BAD_REQUEST, message("BAD_REQUEST_JSON", "请求体格式错误"));
    }

    /** 缺少必填参数 / 参数类型不匹配。 */
    @ExceptionHandler({MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<Void> handleBadRequest(Exception ex, HttpServletRequest request) {
        log.warn("请求参数错误 {}: {}", request.getRequestURI(), ex.getMessage());
        return ApiResult.error(CODE_BAD_REQUEST,
                message(ResultCode.BAD_REQUEST.getI18nKey(), ResultCode.BAD_REQUEST.getMsg()));
    }

    /** 上传文件超过 spring.servlet.multipart.max-file-size 限制。 */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    @ResponseStatus(HttpStatus.PAYLOAD_TOO_LARGE)
    public ApiResult<Void> handleUploadTooLarge(MaxUploadSizeExceededException ex, HttpServletRequest request) {
        log.warn("上传文件过大 {}: {}", request.getRequestURI(), ex.getMessage());
        return ApiResult.error(String.valueOf(WebResultCode.PAYLOAD_TOO_LARGE.getCode()),
                message(WebResultCode.PAYLOAD_TOO_LARGE.getI18nKey(), WebResultCode.PAYLOAD_TOO_LARGE.getMsg()));
    }

    /** Content-Type 不匹配，例如用 application/x-www-form-urlencoded 提交 JSON 接口。 */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseStatus(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
    public ApiResult<Void> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex,
                                                       HttpServletRequest request) {
        log.warn("媒体类型不支持 {}: {}", request.getRequestURI(), ex.getMessage());
        return ApiResult.error(String.valueOf(WebResultCode.UNSUPPORTED_MEDIA_TYPE.getCode()),
                message(WebResultCode.UNSUPPORTED_MEDIA_TYPE.getI18nKey(),
                        WebResultCode.UNSUPPORTED_MEDIA_TYPE.getMsg()));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ApiResult<Void> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                     HttpServletRequest request) {
        log.warn("请求方法不允许 {} {}: {}", request.getMethod(), request.getRequestURI(), ex.getMessage());
        return ApiResult.error(CODE_METHOD_NOT_ALLOWED,
                message(ResultCode.METHOD_NOT_ALLOWED.getI18nKey(), ResultCode.METHOD_NOT_ALLOWED.getMsg()));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResult<Void> handleNotFound(NoResourceFoundException ex, HttpServletRequest request) {
        log.debug("资源不存在 {}: {}", request.getRequestURI(), ex.getMessage());
        return ApiResult.error(CODE_NOT_FOUND,
                message(ResultCode.NOT_FOUND.getI18nKey(), ResultCode.NOT_FOUND.getMsg()));
    }

    /** 兜底异常：日志打印完整堆栈，响应默认只给通用文案，可通过配置放开。 */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResult<Void> handleUnexpected(Exception ex, HttpServletRequest request) {
        log.error("未处理异常 {} {}", request.getMethod(), request.getRequestURI(), ex);
        String message = properties.exposeExceptionMessage() && ex.getMessage() != null && !ex.getMessage().isBlank()
                ? ex.getMessage()
                : message(ResultCode.INTERNAL_SERVER_ERROR.getI18nKey(),
                        ResultCode.INTERNAL_SERVER_ERROR.getMsg());
        return ApiResult.error(CODE_INTERNAL_ERROR, message);
    }

    /** 按当前请求 Locale 解析文案，key 不存在时退回默认文案。 */
    private String message(String key, String defaultMessage) {
        return messageSource.getMessage(key, null, defaultMessage, LocaleContextHolder.getLocale());
    }

    private FieldViolation toViolation(ConstraintViolation<?> cv) {
        String field = cv.getPropertyPath().toString();
        // 方法级校验路径形如 "query.keyword"，去掉方法名前缀让字段名更干净
        int dot = field.indexOf('.');
        if (dot >= 0) {
            field = field.substring(dot + 1);
        }
        Set<String> payload = cv.getConstraintDescriptor().getPayload().stream()
                .map(Class::getSimpleName)
                .collect(Collectors.toSet());
        return new FieldViolation(field, cv.getMessage(),
                sensitiveMatcher.mask(field, cv.getInvalidValue()), payload);
    }
}
