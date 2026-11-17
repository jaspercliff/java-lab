package com.jasper.springdoc.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 知识点：Webhook / Callback 的请求体——由 {@code @Callback} 描述服务端反向调用的地址与载荷。 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "书籍事件回调载荷")
public class BookEventCallback {

    @Schema(description = "事件 ID", example = "evt_1001")
    private String eventId;

    @Schema(description = "事件类型", example = "book.created", allowableValues = {"book.created", "book.updated"})
    private String type;

    @Schema(description = "书籍 ID", example = "1001")
    private Long bookId;
}
