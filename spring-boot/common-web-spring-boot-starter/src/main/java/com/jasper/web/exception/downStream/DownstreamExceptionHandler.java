package com.jasper.web.exception.downStream;

import com.jasper.result.ApiResult;
import com.jasper.result.ResultCode;
import com.jasper.web.constants.WebResultCode;
import com.jasper.web.support.I18nMessages;
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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

/**
 * RestClient / RestTemplate 调用下游失败时，归一化成本地错误码：
 * 有下游状态码的统一按 502，无响应（超时、连接失败）按 503，避免下游原始错误码直接透给前端。
 */
@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class DownstreamExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(RestClientResponseException.class)
    @ResponseStatus(HttpStatus.BAD_GATEWAY)
    public ApiResult<DownstreamError> handleResponseException(RestClientResponseException ex,
                                                              HttpServletRequest request) {
        log.warn("下游调用失败 [{}] status={}", request.getRequestURI(), ex.getStatusCode());
        return ApiResult.error(String.valueOf(WebResultCode.DOWNSTREAM_ERROR.getCode()),
                I18nMessages.get(messageSource, WebResultCode.DOWNSTREAM_ERROR.getI18nKey(),
                        WebResultCode.DOWNSTREAM_ERROR.getMsg()),
                new DownstreamError(ex.getStatusCode().value()));
    }

    @ExceptionHandler(RestClientException.class)
    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    public ApiResult<Void> handleClientException(RestClientException ex, HttpServletRequest request) {
        log.warn("下游不可用 [{}] {}: {}", request.getRequestURI(), ex.getClass().getSimpleName(), ex.getMessage());
        return ApiResult.error(String.valueOf(ResultCode.SERVICE_UNAVAILABLE.getCode()),
                I18nMessages.get(messageSource, ResultCode.SERVICE_UNAVAILABLE.getI18nKey(),
                        ResultCode.SERVICE_UNAVAILABLE.getMsg()));
    }
}
