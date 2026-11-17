package com.jasper.validation.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 知识点：级联对象——本身也是一个被校验的 Java Bean。 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @NotBlank(message = "省份不能为空")
    private String province;

    @NotBlank(message = "城市不能为空")
    @Size(max = 20, message = "城市名不能超过 20 个字符")
    private String city;
}
