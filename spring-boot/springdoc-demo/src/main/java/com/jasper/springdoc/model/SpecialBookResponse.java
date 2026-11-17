package com.jasper.springdoc.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/** 知识点：继承 + discriminator 子类（见 {@link BookResponse} 类上的 subTypes）。 */
@Data
@EqualsAndHashCode(callSuper = true)
@Schema(description = "限量版书籍，继承 BookResponse", allOf = {BookResponse.class})
public class SpecialBookResponse extends BookResponse {

    @Schema(description = "鉴别器字段", example = "special", allowableValues = {"special"})
    private String type = "special";

    @Schema(description = "限量编号", example = "NO.001")
    private String serialNo;
}
