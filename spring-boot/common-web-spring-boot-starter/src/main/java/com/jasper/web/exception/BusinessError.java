package com.jasper.web.exception;

/**
 * 业务异常的附加信息，放进 ApiResult.data 返回给前端。
 *
 * <p>BusinessException 的 code 是 String（如 ORDER_001），
 * 而 ApiResult.code 是 int，因此把业务码放在 data 中保留。
 *
 * @param code 业务异常码
 */
public record BusinessError(String code) {
}
