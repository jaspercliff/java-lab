package com.jasper.springdoc.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** multipart 上传结果。 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "文件上传结果")
public class UploadResult {

    @Schema(description = "原始文件名", example = "cover.png")
    private String filename;

    @Schema(description = "字节数", example = "20480", minimum = "0")
    private long size;

    @Schema(description = "Content-Type", example = "image/png")
    private String contentType;
}
