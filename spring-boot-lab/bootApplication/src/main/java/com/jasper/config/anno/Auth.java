package com.jasper.config.anno;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE}) // 支持加在方法上，也支持加在类上
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Auth {
    // 可以扩展一些属性，比如 requiredRoles = {"ADMIN"}
}