package com.jasper.validation.model;

import com.jasper.validation.constraint.DateRange;
import com.jasper.validation.constraint.FatalPayload;
import com.jasper.validation.constraint.anno.Phone;
import com.jasper.validation.constraint.anno.UniqueUsername;
import com.jasper.validation.constraint.group.CreateGroup;
import com.jasper.validation.constraint.group.UpdateGroup;
import com.jasper.validation.enums.GenderEnum;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

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
@DateRange(start = "activeFrom", end = "activeTo") // message 用注解默认的 {key}，便于 i18n
public class UserCreateRequest {

    /** 更新场景才必填：同一字段在不同组有不同规则。 */
    @NotNull(message = "更新时 id 不能为空", groups = UpdateGroup.class)
    private Long id;

    /** 不写 groups → 属于 Default 组，任何 {@code @Validated(...)} 都会带上 Default 以外的组时不再校验它。 */
    @NotBlank(message = "用户名不能为空", groups = CreateGroup.class)
    @Size(min = 3, max = 20, message = "用户名长度必须在 3~20 之间", groups = CreateGroup.class)
    @UniqueUsername(groups = CreateGroup.class)
    private String username;

    /** 手机号：nullable=false 表示空值也报错（与 @NotBlank 二选一，这里演示「约束自带非空」）。 */
    @Phone(nullable = false, payload = FatalPayload.class)
    private String phone;

    @Email(message = "邮箱格式不正确")
    private String email;

    @Pattern(regexp = "^[A-Za-z0-9_]{6,16}$", message = "密码只能是 6~16 位字母数字下划线")
    private String password;

    /** 整数码枚举：实现 IEnum，Jackson 按 code 序列化；这里用 @NotNull + 自定义 @AssertTrue 双重校验。 */
    @NotNull(message = "性别不能为空")
    private GenderEnum gender;

    @Min(value = 1, message = "年龄不能小于 1")
    @Max(value = 120, message = "年龄不能大于 120")
    private Integer age;

    @Past(message = "生日必须是过去日期")
    private LocalDate birthday;


    /** 级联校验：不加 @Valid，Address 内部的 @NotBlank 完全不会触发。 */
    @Valid
    @NotNull(message = "地址不能为空")
    private Address address;

    /** 集合元素级联：@Valid 写在泛型参数上，逐个校验 List 里的 Address。 */
    @Valid
    @NotEmpty(message = "收货地址列表不能为空")
    private List<Address> shippingAddresses;

    private LocalDate activeFrom;
    private LocalDate activeTo;

    /** 类内逻辑约束：getter 形式的 @AssertTrue，方法名必须以 is 开头。 */
    @AssertTrue(message = "必须同意用户协议")
    public boolean isAgreed() {
        return agreed;
    }

    private boolean agreed;

    @Builder.Default
    private boolean internal = true;
}
