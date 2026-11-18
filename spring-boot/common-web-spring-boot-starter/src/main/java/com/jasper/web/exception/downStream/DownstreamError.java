package com.jasper.web.exception.downStream;

/**
 * 下游调用失败的附加信息，放进 ApiResult.data，便于排查但不泄漏下游原始报文。
 *
 * @param downstreamStatus 下游返回的 HTTP 状态码（无响应时为 -1）
 */
public record DownstreamError(int downstreamStatus) {
}
