package com.jasper.web.exception.downStream;

import com.jasper.result.ApiResult;
import com.jasper.web.constants.WebResultCode;
import com.jasper.web.support.I18nMessages;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Feign 调用下游失败时与 RestClient 保持一致的错误码，仅在 classpath 存在 Feign 时装配。 */
@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class FeignExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(FeignException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ApiResult<DownstreamError> handleFeign(FeignException ex, HttpServletRequest request) {
        log.warn("Feign 下游调用失败 [{}] status={}: {}", request.getRequestURI(), ex.status(), ex.getMessage());
        return ApiResult.error(String.valueOf(WebResultCode.DOWNSTREAM_ERROR.getCode()),
                I18nMessages.get(messageSource, WebResultCode.DOWNSTREAM_ERROR.getI18nKey(),
                        WebResultCode.DOWNSTREAM_ERROR.getMsg()),
                new DownstreamError(ex.status()));
    }
}
