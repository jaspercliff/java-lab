package com.jasper.springdoc.model;

/** {@code @JsonView} 分组：springdoc 会按 view 输出不同的 schema 视图。 */
public final class Views {

    private Views() {
    }

    /** 摘要视图：列表接口只返回少量字段。 */
    public interface Summary {
    }

    /** 管理视图：包含内部字段。 */
    public interface Admin extends Summary {
    }
}
