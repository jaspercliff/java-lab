package com.jasper.security.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.jasper.security.core.SensitiveSerializer;
import com.jasper.security.core.SensitiveType;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记字段在 JSON 序列化时脱敏，无需全局配置，按需打在 DTO 字段上即可。
 *
 * <pre>{@code
 * @Sensitive                       // ******
 * private String password;
 *
 * @Sensitive(SensitiveType.PHONE)  // 138****1234
 * private String phone;
 * }</pre>
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@JacksonAnnotationsInside
@JsonSerialize(using = SensitiveSerializer.class)
public @interface Sensitive {

    /** 脱敏策略，默认全遮蔽。 */
    SensitiveType value() default SensitiveType.MASK;
}
