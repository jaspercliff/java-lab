package com.jasper.validation.constraint.group;

/**
 * 知识点：分组校验——新增场景。
 *
 * <p>DTO 字段上写 {@code @NotBlank(groups = CreateGroup.class)} 表示只在「新增」组生效。
 * Controller 上用 {@code @Validated(CreateGroup.class)} 指定当前请求使用哪一组。
 * <br>
 * for: 同一个字段，在不同业务场景下，需要执行不同的校验规则
 */
public interface CreateGroup {
}
