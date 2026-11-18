package com.jasper.config;

import com.jasper.dto.FieldViolation;
import com.jasper.exception.BusinessException;
import com.jasper.result.ApiResult;
import com.jasper.result.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 知识点：三种校验异常来源要分别处理——
 *
 * <ol>
 *   <li>{@link MethodArgumentNotValidException}：Controller 的 {@code @Valid @RequestBody} 失败，
 *       携带 BindingResult，字段错误最全；</li>
 *   <li>{@link HandlerMethodValidationException}：Spring 6.1+ 对 Controller 方法参数上的约束
 *       （{@code @RequestParam @Min} 等，类上没标 {@code @Validated} 时）直接抛的新异常；</li>
 *   <li>{@link ConstraintViolationException}：Service 方法级 {@code @Validated} 失败，
 *       或 Controller 类上标了 {@code @Validated} 时方法参数校验失败。</li>
 * </ol>
 *
 * <p>响应统一用工程约定 common 的 {@link ApiResult}：code 取 {@link ResultCode}，
 * violations 放进 data（ApiResult.error 工厂不带 data，故用 record 构造器），
 * traceId 由 common-trace-spring-boot-starter 写入 MDC，随响应一起返回。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // =================  处理自定义业务异常 =================

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK) // 业务异常通常返回 200，由 code 区分状态
    public ApiResult<Void> handleBusinessException(BusinessException e, HttpServletRequest request) {
        // 业务异常属于预期内的错误，使用 warn 级别，不打印堆栈
        log.warn("业务异常 -> URI: {}, Code: {}, Msg: {}", request.getRequestURI(), e.getCode(), e.getMessage());
        return ApiResult.error(Integer.parseInt(e.getCode()), e.getMessage());
    }

    /**
     * 处理 @RequestParam / @PathVariable 参数校验失败或绑定异常
     */
    @ExceptionHandler({BindException.class, MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<Void> handleBindException(Exception e) {
        String message = switch (e) {
            case BindException bindEx -> bindEx.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining("; "));
            case MissingServletRequestParameterException missEx -> "缺少必填参数: " + missEx.getParameterName();
            case MethodArgumentTypeMismatchException typeEx -> "参数类型错误: " + typeEx.getName();
            default -> "参数绑定异常";
        };
        log.warn("参数绑定异常: {}", message);
        return ApiResult.error(400, message);
    }
    /**
     * ① @Valid @RequestBody 失败：从 BindingResult 拿字段错误。
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<List<FieldViolation>> handleBody(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<FieldViolation> violations = new ArrayList<>();
        ex.getBindingResult()
                //获取所有字段错误
                .getFieldErrors().forEach(fe -> violations.add(FieldViolation.builder()
                .field(fe.getField())
                .message(fe.getDefaultMessage())
                .rejectedValue(fe.getRejectedValue())
                // .payload(Set.of()) // BindingResult 不暴露 payload；要 payload 得走方法级校验
                .build()));
                //获取类级别校验错误
        ex.getBindingResult().getGlobalErrors().forEach(oe -> violations.add(FieldViolation.builder()
                .field(oe.getObjectName())
                .message(oe.getDefaultMessage())
                .build()));
        return badRequest(req, "VALIDATION_BODY", "请求参数校验失败", violations);
    }

    /**
     * ② Spring 6.1+ (boot3.2+)方法参数内置校验（Controller 类上无 @Validated 时）。
     * 在 Controller 方法参数上直接声明校验注解，不需要额外添加 @Validated，也不需要 @Valid
     */
    @ExceptionHandler(HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<List<FieldViolation>> handleMethodParams(HandlerMethodValidationException ex,
                                                              HttpServletRequest req) {
        // Spring 6.2 新 API：getParameterValidationResults()（getAllValidationResults() 已废弃待移除）
        List<FieldViolation> violations = ex.getParameterValidationResults().stream()
                .flatMap(result -> result.getResolvableErrors().stream()
                        .map(err -> FieldViolation.builder()
                                .field(result.getMethodParameter().getParameterName())
                                .message(err.getDefaultMessage())
                                .rejectedValue(result.getArgument())
                                .payload(Set.of())
                                .build()))
                .toList();
        return badRequest(req, "VALIDATION_PARAM", "方法参数校验失败", violations);
    }

    /**
     * ③ Service 方法级 @Validated 失败：从 ConstraintViolation 能拿到 payload，可分级处理。
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<List<FieldViolation>> handleConstraint(ConstraintViolationException ex,
                                                            HttpServletRequest req) {
        List<FieldViolation> violations = ex.getConstraintViolations().stream()
                .map(this::toViolation)
                .toList();
        return badRequest(req, "VALIDATION_METHOD", "方法入参校验失败", violations);
    }


    // =================  处理常见 Web/框架异常 (利用 JDK 21 Switch 模式匹配) =================

    @ExceptionHandler({
            HttpRequestMethodNotSupportedException.class,
            HttpMessageNotReadableException.class,
            NoResourceFoundException.class // Spring Boot 3.2+ 替代了老的 NoHandlerFoundException
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResult<Void> handleWebException(Exception e) {
        // JDK 21 ：直接在 switch 中进行类型匹配并提取变量
        String message = switch (e) {
            case HttpRequestMethodNotSupportedException ex -> {
                log.warn("请求方法不支持: {}", ex.getMessage());
                yield "不支持的请求方法: " + ex.getMethod();
            }
            case HttpMessageNotReadableException ex -> {
                log.warn("请求体解析失败: {}", ex.getMessage());
                yield "请求参数格式错误 (如 JSON 格式不正确)";
            }
            case NoResourceFoundException ex -> {
                log.warn("资源不存在: {}", ex.getResourcePath());
                yield "请求的资源不存在";
            }
            default -> "请求参数或格式错误";
        };

        return ApiResult.error(400, message);
    }

    private FieldViolation toViolation(ConstraintViolation<?> cv) {
        String field = cv.getPropertyPath().toString();
        // 方法级校验的路径形如 "query.keyword"，去掉方法名前缀让字段名更干净
        int dot = field.indexOf('.');
        if (dot >= 0) {
            field = field.substring(dot + 1);
        }
        Set<String> payload = cv.getConstraintDescriptor().getPayload().stream()
                .map(Class::getSimpleName)
                .collect(Collectors.toSet());
        return FieldViolation.builder()
                .field(field)
                .message(cv.getMessage())
                .rejectedValue(cv.getInvalidValue())
                .payload(payload)
                .build();
    }

    /**
     * 把三类异常收敛到同一个 ApiResult：业务码 400，错误来源标签放 message 前缀。
     */
    private ApiResult<List<FieldViolation>> badRequest(HttpServletRequest req, String codeTag, String message,
                                                       List<FieldViolation> violations) {
        log.warn("参数校验失败 [{}] {}: {}", codeTag, req.getRequestURI(), violations);
        // ApiResult.error() 不带 data；这里用 record 构造器把 violations 放进 data

        return ApiResult.error(ResultCode.BAD_REQUEST.getCode(),
                codeTag + ": " + message,
                violations);
    }
}
