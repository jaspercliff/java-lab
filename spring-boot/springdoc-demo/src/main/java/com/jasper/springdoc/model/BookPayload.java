package com.jasper.springdoc.model;

import io.swagger.v3.oas.annotations.media.DiscriminatorMapping;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 知识点：多态 schema —— {@code oneOf} + {@code discriminatorProperty} + {@code discriminatorMapping}。
 * springdoc 2.10 可用 {@code springdoc.model-converters.polymorphic-converter.one-of-as-ref=true}
 * 把 oneOf 抽成可复用组件。
 */
@Schema(
        description = "载荷基类（多态）",
        oneOf = {TextPayload.class, ImagePayload.class},
        subTypes = {TextPayload.class, ImagePayload.class},
        discriminatorProperty = "kind",
        discriminatorMapping = {
                @DiscriminatorMapping(value = "text", schema = TextPayload.class),
                @DiscriminatorMapping(value = "image", schema = ImagePayload.class)
        }
)
public abstract class BookPayload {

    private String kind;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }
}
