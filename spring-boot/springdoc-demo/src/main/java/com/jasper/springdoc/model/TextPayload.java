package com.jasper.springdoc.model;

import io.swagger.v3.oas.annotations.media.Schema;

/** oneOf 分支一。 */
@Schema(description = "文本载荷", allOf = {BookPayload.class})
public class TextPayload extends BookPayload {

    @Schema(description = "文本内容", example = "hello openapi")
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
