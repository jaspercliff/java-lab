
package com.jasper.quartz.exception;

import com.jasper.result.ApiResponse;
import com.jasper.result.ResultCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;
import org.quartz.UnableToInterruptJobException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.NoSuchElementException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Job / Trigger 不存在
     */
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(
            NoSuchElementException ex,
            HttpServletRequest request) {

        return build(
                HttpStatus.NOT_FOUND,
                ResultCode.NOT_FOUND,
                ex.getMessage()
        );
    }

    /**
     * 非法参数：Cron、Job 类、时区等
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(
            IllegalArgumentException ex,
            HttpServletRequest request) {

        return build(
                HttpStatus.BAD_REQUEST,
                ResultCode.BAD_REQUEST,
                ex.getMessage()
        );
    }

    /**
     * Job 当前状态不允许中断
     */
    @ExceptionHandler(UnableToInterruptJobException.class)
    public ResponseEntity<ApiResponse<Void>> handleUnableToInterrupt(
            UnableToInterruptJobException ex,
            HttpServletRequest request) {

        return build(
                HttpStatus.CONFLICT,
                ResultCode.CONFLICT,
                ex.getMessage()
        );
    }

    /**
     * Quartz 调度器异常
     */
    @ExceptionHandler(SchedulerException.class)
    public ResponseEntity<ApiResponse<Void>> handleScheduler(
            SchedulerException ex,
            HttpServletRequest request) {

        log.error(
                "Scheduler 操作失败 uri={}",
                request.getRequestURI(),
                ex
        );

        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ResultCode.SCHEDULER_ERROR,
                "调度器操作失败"
        );
    }

    /**
     * 未知异常兜底
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(
            Exception ex,
            HttpServletRequest request) {

        log.error(
                "系统发生未处理异常 uri={}",
                request.getRequestURI(),
                ex
        );

        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ResultCode.INTERNAL_SERVER_ERROR,
                ResultCode.INTERNAL_SERVER_ERROR.getMsg()
        );
    }

    private ResponseEntity<ApiResponse<Void>> build(
            HttpStatus status,
            ResultCode resultCode,
            String message) {

        ApiResponse<Void> response = ApiResponse.error(
                resultCode.getCode(),
                message
        );

        return ResponseEntity.status(status).body(response);
    }
}
