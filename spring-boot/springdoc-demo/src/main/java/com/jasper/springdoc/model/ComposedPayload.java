package com.jasper.springdoc.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * A10 演示：把组合 schema 关键字挂在返回类型上，而不是挂在响应的 Content 上。
 *
 * <p>改为统一 {@code ApiResult<T>} 包裹后，响应根节点是通用结构，
 * 业务载荷的类型信息应放在 data 对应的模型上，这样 Swagger UI 才能正确展示
 * {@code anyOf} 与 OpenAPI 3.1 的 {@code types} 多类型。
 */
@Schema(
        description = "组合 schema 根节点（anyOf）",
        types = {"object", "null"},
        anyOf = {TextPayload.class, ImagePayload.class}
)
public class ComposedPayload {
}
