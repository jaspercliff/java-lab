package com.jasper.result;

import com.jasper.api.IEnum;

/**
 * 业务错误码，与系统错误码 {@link IResultCode} 分开。
 *
 * <p>约定：
 * <ul>
 *     <li>{@link #getCode()} 返回业务码，如 {@code RB1001}，直接作为 {@link ApiResult#code()}；</li>
 *     <li>{@link #getDesc()} 返回默认描述，满足 {@link IEnum}；</li>
 *     <li>{@link #getMsg()} 返回国际化 key，默认与业务码相同（如 {@code RB1001 = stock not enough}）。</li>
 * </ul>
 */
public interface IBusinessResultCode extends IEnum<String> {

    /** 国际化 key，默认与业务码一致。 */
    default String getMsg() {
        return getCode();
    }
}
