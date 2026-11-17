package com.jasper.validation.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

/**
 * 知识点：方法级校验——在普通 Bean 上标 {@link Validated}，Spring AOP 会拦截方法，
 * 校验入参上的约束，失败抛 {@code ConstraintViolationException}（不同于 Controller 的
 * {@code MethodArgumentNotValidException}）。
 *
 * <p>触发前提：类上有 {@code @Validated} 且容器中存在 MethodValidationPostProcessor
 * （Boot 检测到 jakarta.validation 自动注册）。
 */
@Slf4j
@Validated
@Service
public class MethodValidatedService {

    /**
     * 参数约束直接写在形参上；多个参数用 @Validated 类级注解统一开启。
     * 方法校验失败会抛 ConstraintViolationException，由全局异常处理器转成 400。
     */
    public String query(@NotBlank(message = "关键字不能为空") String keyword,
                        @Min(value = 1, message = "页码从 1 开始") int page,
                        @Min(value = 1, message = "每页至少 1 条") int size) {
        return "keyword=" + keyword + ", page=" + page + ", size=" + size;
    }

    /** 集合元素级联：@Valid 标在泛型参数上，逐个校验 List 里的元素。 */
    public int countAddresses(@Valid @NotNull List<AddressForValidation> addresses) {
        return addresses.size();
    }

    /** 简单的入参 Bean，仅用于演示方法参数级联。 */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AddressForValidation {
        @NotBlank(message = "城市不能为空")
        @Size(max = 20)
        private String city;
    }
}
