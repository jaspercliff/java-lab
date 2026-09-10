package com.jasper.config;

import com.jasper.exception.BusinessException;
import com.jasper.result.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ================= 1. 处理自定义业务异常 =================

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK) // 业务异常通常返回 200，由 code 区分状态
    public ApiResponse<Void> handleBusinessException(BusinessException e, HttpServletRequest request) {
        // 业务异常属于预期内的错误，使用 warn 级别，不打印堆栈
        log.warn("业务异常 -> URI: {}, Code: {}, Msg: {}", request.getRequestURI(), e.getCode(), e.getMessage());
        return ApiResponse.error(Integer.parseInt(e.getCode()), e.getMessage());
    }

    // ================= 2. 处理参数校验异常 (@Valid / @Validated) =================

    /**
     * 处理 @RequestBody 参数校验失败
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleValidationException(MethodArgumentNotValidException e) {
        // 提取所有校验失败的字段和错误信息
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining("; "));
        
        log.warn("参数校验失败: {}", message);
        return ApiResponse.error(400, message);
    }

    /**
     * 处理 @RequestParam / @PathVariable 参数校验失败或绑定异常
     */
    @ExceptionHandler({BindException.class, MissingServletRequestParameterException.class, MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleBindException(Exception e) {
        String message = switch (e) {
            case BindException bindEx -> bindEx.getFieldErrors().stream()
                    .map(FieldError::getDefaultMessage)
                    .collect(Collectors.joining("; "));
            case MissingServletRequestParameterException missEx -> "缺少必填参数: " + missEx.getParameterName();
            case MethodArgumentTypeMismatchException typeEx -> "参数类型错误: " + typeEx.getName();
            default -> "参数绑定异常";
        };
        
        log.warn("参数绑定异常: {}", message);
        return ApiResponse.error(400, message);
    }

    // ================= 3. 处理常见 Web/框架异常 (利用 JDK 21 Switch 模式匹配) =================

    @ExceptionHandler({
            HttpRequestMethodNotSupportedException.class,
            HttpMessageNotReadableException.class,
            NoResourceFoundException.class // Spring Boot 3.2+ 替代了老的 NoHandlerFoundException
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse<Void> handleWebException(Exception e) {
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

        return ApiResponse.error(400, message);
    }

    // ================= 4. 兜底处理所有未知异常 =================

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse<Void> handleException(Exception e, HttpServletRequest request) {
        // 未知异常必须打印完整堆栈，使用 error 级别
        log.error("系统未知异常 -> URI: {}", request.getRequestURI(), e);
        
        // 生产环境中，未知异常绝对不能把真实错误信息返回给前端，防止安全漏洞
        return ApiResponse.error(500, "系统繁忙，请稍后再试");
    }
}