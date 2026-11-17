package com.jasper.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 知识点大集合：一个 DTO 覆盖常用 jakarta 内置约束 + 分组 + 级联 + 类级约束 + 自定义约束。
 *
 * <p>内置约束速查：
 * <ul>
 *   <li>空值类：{@code @NotNull}(对象) / {@code @NotBlank}(字符串去空格后非空) / {@code @NotEmpty}(集合/数组/Map)</li>
 *   <li>长度/范围：{@code @Size} / {@code @Min} / {@code @Max} / {@code @DecimalMin} / {@code @Digits}</li>
 *   <li>格式：{@code @Email} / {@code @Pattern} / {@code @URL}(HV 扩展) / {@code @Past} / {@code @Future}</li>
 *   <li>逻辑：{@code @AssertTrue} / {@code @Positive} / {@code @Negative}</li>
 * </ul>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreateRequest {

    private Long id;

    @NotBlank(message = "{BUS0001}")
    private String username;

}