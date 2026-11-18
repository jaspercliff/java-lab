package com.jasper.security.annotation;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.jasper.security.core.SensitiveSerializer;
import com.jasper.security.core.SensitiveType;
import com.jasper.security.core.SensitiveValueSerializer;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记字段在 JSON 序列化时脱敏，无需全局配置，按需打在 DTO 字段上即可。
 *
 * <p>同时兼容 Jackson 2（Spring Boot 3.5 内置）与 Jackson 3（common 库升级到 3.x）：
 * 两个全限定名的 {@code @JsonSerialize} 分别指向各自的序列化器，两套注解在 Jar 中共存，
 * 由使用者 classpath 上的 ObjectMapper 决定哪套生效。
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
@com.fasterxml.jackson.databind.annotation.JsonSerialize(using = SensitiveSerializer.class)
@tools.jackson.databind.annotation.JsonSerialize(using = SensitiveValueSerializer.class)
public @interface Sensitive {

    /** 脱敏策略，默认全遮蔽。 */
    SensitiveType value() default SensitiveType.MASK;
}
