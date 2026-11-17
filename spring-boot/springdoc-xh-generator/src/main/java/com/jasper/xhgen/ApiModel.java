package com.jasper.xhgen;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * OpenAPI 解析后的轻量模型。
 *
 * <p>只保留生成 xh 脚本需要的字段，不引入 swagger-models —— 省掉一整套依赖，
 * 也避免不同 springdoc / OpenAPI 版本之间的模型类冲突。
 */
public final class ApiModel {

    private ApiModel() {
    }

    /**
     * 一个可命令行传入的参数。
     *
     * @param name         原始参数名（path/query/header/cookie/body 字段名）
     * @param defaultValue 原始默认值（未做 shell 转义）
     * @param itemTemplate xh 请求项模板，{@code {v}} 会被替换成 {@code ${N-default}}；
     *                     {@code @path} 表示只进 URL；{@code @json} 表示整块 JSON body；
     *                     {@code null} 表示 --raw 的原始数据体
     * @param comment      参数说明（用于脚本里的位置注释）
     */
    public record ApiArg(
            String name,
            String defaultValue,
            String itemTemplate,
            String comment
    ) {
    }

    /**
     * 一个接口（OpenAPI Operation）。
     *
     * @param id          operationId，用作函数名 / 脚本文件名
     * @param method      HTTP 方法（大写）
     * @param path        OpenAPI path，路径参数仍是 {@code {name}} 原样
     * @param summary     接口摘要
     * @param args        按「路径参数 → query → header → cookie → body」顺序排列
     * @param bodyFlag    xh 请求体开关：空 / {@code --form} / {@code --multipart} / {@code --raw}
     * @param deprecated  是否标记废弃
     */
    public record ApiOperation(
            String id,
            String method,
            String path,
            String summary,
            List<ApiArg> args,
            String bodyFlag,
            boolean deprecated
    ) {
    }

    /**
     * 解析结果。
     *
     * @param groups    分组名 → 该分组下的接口（保持 spec 中的顺序）
     * @param schemes   components.securitySchemes 中出现的鉴权类型关键词
     * @param baseUrl   脚本默认 BASE_URL
     */
    public record ParsedSpec(
            LinkedHashMap<String, List<ApiOperation>> groups,
            List<String> schemes,
            String baseUrl
    ) {
    }
}
