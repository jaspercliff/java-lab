package com.jasper.web.constants;

import com.jasper.api.IEnum;
import com.jasper.result.IResultCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Web 层扩展错误码。
 *
 * <p>同时实现 {@link IResultCode}（作为 ApiResult.code）和 {@link IEnum}（支持 parseOf 查找）。
 */
@Getter
@AllArgsConstructor
public enum WebResultCode implements IResultCode, IEnum<Integer> {

    PAYLOAD_TOO_LARGE(413, "上传文件过大"),
    UNSUPPORTED_MEDIA_TYPE(415, "不支持的媒体类型"),
    DOWNSTREAM_ERROR(502, "下游服务调用失败");

    private final Integer code;
    private final String msg;

    @Override
    public String getDesc() {
        return msg;
    }
}
