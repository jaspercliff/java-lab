package com.jasper.springdoc.model;

import io.swagger.v3.oas.annotations.media.Schema;

/** oneOf 分支二。 */
@Schema(description = "图片载荷", allOf = {BookPayload.class})
public class ImagePayload extends BookPayload {

    @Schema(description = "图片 URL", example = "https://cdn.example.com/1.png", format = "uri")
    private String url;

    @Schema(description = "宽度（像素）", example = "800", minimum = "1")
    private int width;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
}
