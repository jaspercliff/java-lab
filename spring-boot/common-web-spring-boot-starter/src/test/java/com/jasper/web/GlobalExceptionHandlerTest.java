package com.jasper.web;

import com.jasper.exception.BusinessException;
import com.jasper.result.ApiResult;
import com.jasper.result.IBusinessResultCode;
import com.jasper.result.ResultCode;
import com.jasper.security.core.SensitiveFieldMatcher;
import com.jasper.web.config.properties.WebProperties;
import com.jasper.web.exception.BusinessError;
import com.jasper.web.exception.FieldViolation;
import com.jasper.web.exception.GlobalExceptionHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(ChineseLocaleExtension.class)
class GlobalExceptionHandlerTest {

    private static final SensitiveFieldMatcher MATCHER = new SensitiveFieldMatcher(
            List.of("password", "pwd", "token", "secret", "phone", "idCard"));

    private final HttpServletRequest request = mock(HttpServletRequest.class);

    @Test
    void businessExceptionKeepsStringBusinessCodeInData() {
        when(request.getRequestURI()).thenReturn("/order");
        GlobalExceptionHandler handler = new GlobalExceptionHandler(new WebProperties(true, false), MATCHER, TestMessages.source());

        ApiResult<BusinessError> result = handler.handleBusiness(
                new BusinessException("ORDER_001", "库存不足"), request);

        assertThat(result.code()).isEqualTo("ORDER_001");
        assertThat(result.message()).isEqualTo("库存不足");
        assertThat(result.data().code()).isEqualTo("ORDER_001");
    }

    @Test
    void bodyViolationsAreReturnedAsData() throws Exception {
        when(request.getRequestURI()).thenReturn("/user");
        GlobalExceptionHandler handler = new GlobalExceptionHandler(new WebProperties(true, false), MATCHER, TestMessages.source());

        Method method = Target.class.getDeclaredMethod("handle", String.class);
        BeanPropertyBindingResult binding = new BeanPropertyBindingResult(new Object(), "request");
        binding.addError(new FieldError("request", "name", "", false,
                new String[0], new Object[0], "名称不能为空"));
        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(new MethodParameter(method, 0), binding);

        ApiResult<List<FieldViolation>> result = handler.handleBody(ex, request);

        assertThat(result.code()).isEqualTo("400");
        assertThat(result.message()).isEqualTo("请求参数校验失败");
        assertThat(result.data()).singleElement().satisfies(v -> {
            assertThat(v.field()).isEqualTo("name");
            assertThat(v.message()).isEqualTo("名称不能为空");
            assertThat(v.rejectedValue()).isEqualTo("");
        });
    }

    @Test
    void bodyPasswordRejectedValueIsMasked() throws Exception {
        when(request.getRequestURI()).thenReturn("/login");
        GlobalExceptionHandler handler = new GlobalExceptionHandler(new WebProperties(true, false), MATCHER, TestMessages.source());

        Method method = Target.class.getDeclaredMethod("handle", String.class);
        BeanPropertyBindingResult binding = new BeanPropertyBindingResult(new Object(), "request");
        binding.addError(new FieldError("request", "password", "123456", false,
                new String[0], new Object[0], "密码长度不足"));
        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(new MethodParameter(method, 0), binding);

        ApiResult<List<FieldViolation>> result = handler.handleBody(ex, request);

        assertThat(result.data()).singleElement().satisfies(v -> {
            assertThat(v.field()).isEqualTo("password");
            assertThat(v.rejectedValue()).isEqualTo("******");
        });
    }

    @Test
    void constraintViolationPathIsTrimmed() {
        when(request.getRequestURI()).thenReturn("/query");
        GlobalExceptionHandler handler = new GlobalExceptionHandler(new WebProperties(true, false), MATCHER, TestMessages.source());
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Query>> violations = validator.validate(new Query(""));

        ApiResult<List<FieldViolation>> result = handler.handleConstraint(
                new ConstraintViolationException(violations), request);

        assertThat(result.data()).singleElement().satisfies(v ->
                assertThat(v.field()).isEqualTo("keyword"));
    }

    @Test
    void constraintViolationMasksPassword() {
        when(request.getRequestURI()).thenReturn("/login");
        GlobalExceptionHandler handler = new GlobalExceptionHandler(new WebProperties(true, false), MATCHER, TestMessages.source());
        Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
        Set<ConstraintViolation<Credentials>> violations = validator.validate(new Credentials("123456"));

        ApiResult<List<FieldViolation>> result = handler.handleConstraint(
                new ConstraintViolationException(violations), request);

        assertThat(result.data()).singleElement().satisfies(v -> {
            assertThat(v.field()).isEqualTo("password");
            assertThat(v.rejectedValue()).isEqualTo("******");
        });
    }

    @Test
    void unexpectedExceptionHidesMessageByDefault() {
        when(request.getRequestURI()).thenReturn("/secret");
        when(request.getMethod()).thenReturn("GET");
        GlobalExceptionHandler handler = new GlobalExceptionHandler(new WebProperties(true, false), MATCHER, TestMessages.source());

        ApiResult<Void> result = handler.handleUnexpected(new IllegalStateException("密码是 123456"), request);

        assertThat(result.code()).isEqualTo("500");
        assertThat(result.message()).isEqualTo(ResultCode.INTERNAL_SERVER_ERROR.getMsg());
    }

    @Test
    void unexpectedExceptionCanExposeMessageWhenConfigured() {
        when(request.getRequestURI()).thenReturn("/secret");
        when(request.getMethod()).thenReturn("GET");
        GlobalExceptionHandler handler = new GlobalExceptionHandler(new WebProperties(true, true), MATCHER, TestMessages.source());

        ApiResult<Void> result = handler.handleUnexpected(new IllegalStateException("密码是 123456"), request);

        assertThat(result.message()).isEqualTo("密码是 123456");
    }

    @Test
    void resultCodeBusinessExceptionUsesNumericCode() {
        when(request.getRequestURI()).thenReturn("/conflict");
        GlobalExceptionHandler handler = new GlobalExceptionHandler(new WebProperties(true, false), MATCHER, TestMessages.source());

        ApiResult<BusinessError> result = handler.handleBusiness(
                new BusinessException(ResultCode.CONFLICT, "资源冲突"), request);

        assertThat(result.code()).isEqualTo("50000");
        assertThat(result.data().code()).isEqualTo("50000");
    }

    @Test
    void uploadAndMediaTypeExceptionsAreMapped() {
        when(request.getRequestURI()).thenReturn("/upload");
        GlobalExceptionHandler handler = new GlobalExceptionHandler(new WebProperties(true, false), MATCHER, TestMessages.source());

        assertThat(handler.handleUploadTooLarge(new MaxUploadSizeExceededException(1024L), request).code())
                .isEqualTo("413");
        assertThat(handler.handleUnsupportedMediaType(new HttpMediaTypeNotSupportedException("bad type"), request).code())
                .isEqualTo("415");
    }

    @Test
    void businessCodeIsResolvedFromBusinessBasename() {
        when(request.getRequestURI()).thenReturn("/order");
        GlobalExceptionHandler handler = new GlobalExceptionHandler(new WebProperties(true, false),
                MATCHER, TestMessages.source());

        ApiResult<BusinessError> result = handler.handleBusiness(
                new BusinessException(StockCode.STOCK_NOT_ENOUGH), request);

        assertThat(result.code()).isEqualTo("RB9999");
        assertThat(result.message()).isEqualTo("库存不足");
        assertThat(result.data().code()).isEqualTo("RB9999");
    }

    @Test
    void businessMessageFollowsRequestLocale() {
        when(request.getRequestURI()).thenReturn("/order");
        GlobalExceptionHandler handler = new GlobalExceptionHandler(new WebProperties(true, false),
                MATCHER, TestMessages.source());
        Locale original = LocaleContextHolder.getLocale();
        try {
            LocaleContextHolder.setLocale(Locale.ENGLISH);

            ApiResult<BusinessError> result = handler.handleBusiness(
                    new BusinessException(StockCode.STOCK_NOT_ENOUGH), request);

            assertThat(result.message()).isEqualTo("stock not enough");
        } finally {
            LocaleContextHolder.setLocale(original);
        }
    }

    private static class Target {
        void handle(String name) {
        }
    }

    private record Query(@NotBlank String keyword) {
    }

    private record Credentials(@NotBlank @Size(max = 3) String password) {
    }

    private enum StockCode implements IBusinessResultCode {
        STOCK_NOT_ENOUGH("RB9999", "库存不足");

        private final String code;
        private final String desc;

        StockCode(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        @Override
        public String getCode() {
            return code;
        }

        @Override
        public String getDesc() {
            return desc;
        }
    }
}
