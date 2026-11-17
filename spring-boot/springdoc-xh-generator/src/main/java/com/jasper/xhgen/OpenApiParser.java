package com.jasper.xhgen;

import com.jasper.xhgen.ApiModel.ApiArg;
import com.jasper.xhgen.ApiModel.ApiOperation;
import com.jasper.xhgen.ApiModel.ParsedSpec;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 把 springdoc 输出的 OpenAPI JSON 解析成 {@link ParsedSpec}。
 *
 * <p>关注三件事：
 * <ol>
 *   <li>按 tag（springdoc 默认 tag 就是 Controller 名）归类接口；</li>
 *   <li>解析参数（含 {@code $ref}）与请求体示例，生成带默认值的 xh 请求项；</li>
 *   <li>把路径模板 {@code {id}} 换成脚本里的 {@code ${id}}。</li>
 * </ol>
 */
final class OpenApiParser {

    private static final List<String> METHODS =
            List.of("get", "post", "put", "delete", "patch", "head", "options", "trace");

    private static final int MAX_SCHEMA_DEPTH = 4;

    private final ObjectMapper mapper;

    OpenApiParser(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    ParsedSpec parse(String json, String baseUrlOverride) {
        JsonNode root = mapper.readTree(json);
        if (root == null || !root.isObject()) {
            throw new IllegalArgumentException("OpenAPI 文档根节点不是 JSON 对象");
        }

        JsonNode components = root.path("components");
        Map<String, JsonNode> parameterRefs = refIndex(components.path("parameters"));
        Map<String, JsonNode> requestBodyRefs = refIndex(components.path("requestBodies"));
        Map<String, JsonNode> schemaRefs = refIndex(components.path("schemas"));

        LinkedHashMap<String, List<ApiOperation>> groups = new LinkedHashMap<>();
        JsonNode paths = root.path("paths");
        for (Map.Entry<String, JsonNode> pathEntry : paths.properties()) {
            String path = pathEntry.getKey();
            JsonNode pathItem = pathEntry.getValue();
            if (!pathItem.isObject()) {
                continue;
            }
            List<JsonNode> pathLevelParams = parameterList(pathItem.path("parameters"), parameterRefs);

            for (String method : METHODS) {
                JsonNode operation = pathItem.get(method);
                if (operation == null || !operation.isObject()) {
                    continue;
                }

                String group = firstTag(operation);
                String operationId = operationId(operation, method, path);

                List<ApiArg> args = new ArrayList<>();
                Set<String> seenParams = new LinkedHashSet<>();
                List<JsonNode> allParams = new ArrayList<>(pathLevelParams);
                allParams.addAll(parameterList(operation.path("parameters"), parameterRefs));
                for (JsonNode parameter : allParams) {
                    ApiArg arg = parameterArg(parameter, schemaRefs);
                    if (arg == null) {
                        continue;
                    }
                    String key = arg.comment() + "|" + arg.name();
                    if (seenParams.add(key)) {
                        args.add(arg);
                    }
                }

                String bodyFlag = "";
                JsonNode requestBody = resolveRef(operation.path("requestBody"), requestBodyRefs);
                if (requestBody != null && requestBody.isObject()) {
                    String mediaType = pickMediaType(requestBody.path("content"));
                    if (!mediaType.isEmpty()) {
                        bodyFlag = bodyFlag(mediaType);
                        JsonNode content = requestBody.path("content").path(mediaType);
                        List<ApiArg> bodyArgs = bodyArgs(content, mediaType, schemaRefs);
                        for (ApiArg bodyArg : bodyArgs) {
                            if (!seenParams.add("body|" + bodyArg.name())) {
                                continue;
                            }
                            args.add(bodyArg);
                        }
                    }
                }

                groups.computeIfAbsent(group, k -> new ArrayList<>()).add(
                        new ApiOperation(
                                operationId,
                                method.toUpperCase(Locale.ROOT),
                                path,
                                text(operation.path("summary")),
                                args,
                                bodyFlag,
                                operation.path("deprecated").asBoolean(false)
                        )
                );
            }
        }

        List<String> schemes = securitySchemes(components.path("securitySchemes"));
        String baseUrl = baseUrlOverride != null && !baseUrlOverride.isBlank()
                ? stripSlash(baseUrlOverride)
                : stripSlash(firstServer(root.path("servers")));

        return new ParsedSpec(groups, schemes, baseUrl);
    }

    // --------------------------------------------------------------- 参数

    private List<JsonNode> parameterList(JsonNode array, Map<String, JsonNode> refs) {
        List<JsonNode> result = new ArrayList<>();
        if (array == null || !array.isArray()) {
            return result;
        }
        for (JsonNode item : array) {
            JsonNode resolved = resolveRef(item, refs);
            if (resolved != null && resolved.isObject()) {
                result.add(resolved);
            }
        }
        return result;
    }

    private ApiArg parameterArg(JsonNode parameter, Map<String, JsonNode> schemaRefs) {
        String name = text(parameter.path("name"));
        if (name.isEmpty()) {
            return null;
        }
        String in = text(parameter.path("in"));
        if (in.isEmpty()) {
            in = "query";
        }

        JsonNode schema = resolveRef(parameter.path("schema"), schemaRefs);
        String sample = sampleValue(parameter, schema, schemaRefs);

        String template = switch (in) {
            case "path" -> "@path"; // 路径参数只参与 URL 替换，不生成 xh 请求项
            case "query" -> name + "=={v}";
            case "header" -> name + ":{v}";
            case "cookie" -> "Cookie:" + name + "={v}";
            default -> name + "=={v}";
        };
        String comment = switch (in) {
            case "path" -> "路径参数";
            case "query" -> "query";
            case "header" -> "header";
            case "cookie" -> "cookie";
            default -> in;
        };
        return new ApiArg(name, sample, template, comment + " " + name);
    }

    // --------------------------------------------------------------- 请求体

    private String pickMediaType(JsonNode content) {
        if (!content.isObject()) {
            return "";
        }
        for (String candidate : List.of("application/json", "application/problem+json")) {
            if (content.has(candidate)) {
                return candidate;
            }
        }
        for (String candidate : List.of("application/x-www-form-urlencoded", "multipart/form-data")) {
            if (content.has(candidate)) {
                return candidate;
            }
        }
        for (String key : content.propertyNames()) {
            return key;
        }
        return "";
    }

    private String bodyFlag(String mediaType) {
        if (mediaType.startsWith("multipart/")) {
            return "--multipart";
        }
        if (mediaType.equals("application/x-www-form-urlencoded")) {
            return "--form";
        }
        return "";
    }

    private List<ApiArg> bodyArgs(JsonNode content, String mediaType, Map<String, JsonNode> schemaRefs) {
        List<ApiArg> result = new ArrayList<>();

        JsonNode example = firstExample(content);
        JsonNode body = example != null ? example : sampleFromSchema(content.path("schema"), schemaRefs, 0);

        boolean isJson = mediaType.contains("json");
        if (isJson) {
            // JSON：整块写死，交给脚本里的 jaq -n 生成，类型天然正确。
            // 用 pretty 打印，脚本里读起来就是一份看得懂的 JSON。
            String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(body);
            result.add(new ApiArg("body", json, "@json", "body JSON"));
            return result;
        }

        if (!body.isObject()) {
            // 非 JSON 且非对象（如 text/plain）：用 --raw 从 stdin 发送
            String raw = body.isValueNode() ? scalar(body) : body.toString();
            result.add(new ApiArg("body", raw, null, "body 原始数据"));
            return result;
        }

        // form / multipart：逐字段，字段名直接用原始名
        boolean multipart = mediaType.startsWith("multipart/");
        for (Map.Entry<String, JsonNode> property : body.properties()) {
            String name = property.getKey();
            JsonNode value = property.getValue();
            String literal;
            String template;

            if (value.isObject() || value.isArray()) {
                literal = mapper.writeValueAsString(value);
                template = name + ":={v}";
            } else if (isIntegerOrBoolean(value)) {
                literal = scalar(value);
                template = name + ":={v}";
            } else if (multipart && looksLikeFile(name, value)) {
                literal = "./" + name + ".bin";
                template = name + "@{v}";
            } else {
                literal = scalar(value);
                template = name + "={v}";
            }
            result.add(new ApiArg(name, literal, template, "body " + name));
        }
        return result;
    }

    private JsonNode firstExample(JsonNode content) {
        if (content.hasNonNull("example")) {
            return content.path("example");
        }
        JsonNode examples = content.path("examples");
        for (String key : examples.propertyNames()) {
            JsonNode example = examples.path(key).path("value");
            if (!example.isMissingNode()) {
                return example;
            }
        }
        return null;
    }

    // --------------------------------------------------------------- schema 示例

    private String sampleValue(JsonNode parameter, JsonNode schema, Map<String, JsonNode> schemaRefs) {
        JsonNode example = parameter.hasNonNull("example") ? parameter.path("example") : null;
        if (example == null && schema != null && schema.hasNonNull("example")) {
            example = schema.path("example");
        }
        if (example != null) {
            return scalar(example);
        }
        if (schema != null && schema.hasNonNull("default")) {
            return scalar(schema.path("default"));
        }
        if (schema != null && schema.has("enum")) {
            JsonNode values = schema.path("enum");
            if (values.isArray() && !values.isEmpty()) {
                return scalar(values.get(0));
            }
        }
        return placeholder(schema);
    }

    private JsonNode sampleFromSchema(JsonNode schemaNode, Map<String, JsonNode> refs, int depth) {
        JsonNode schema = resolveRef(schemaNode, refs);
        if (schema == null || depth > MAX_SCHEMA_DEPTH) {
            return mapper.valueToTree("");
        }
        if (schema.hasNonNull("example")) {
            return schema.path("example");
        }
        if (schema.hasNonNull("default")) {
            return schema.path("default");
        }
        if (schema.has("enum")) {
            JsonNode values = schema.path("enum");
            if (values.isArray() && !values.isEmpty()) {
                return values.get(0);
            }
        }
        String type = text(schema.path("type"));
        if (type.isEmpty() && schema.path("type").isArray()) {
            // OpenAPI 3.1: "type": ["string", "null"]
            for (JsonNode candidate : schema.path("type")) {
                if (!"null".equals(candidate.stringValue())) {
                    type = candidate.stringValue();
                    break;
                }
            }
        }
        if (type.isEmpty() && schema.has("properties")) {
            type = "object";
        }
        return switch (type) {
            case "object" -> {
                Map<String, Object> object = new LinkedHashMap<>();
                JsonNode properties = schema.path("properties");
                for (String name : properties.propertyNames()) {
                    JsonNode propertySchema = properties.path(name);
                    object.put(name, toJava(sampleFromSchema(propertySchema, refs, depth + 1)));
                }
                yield mapper.valueToTree(object);
            }
            case "array" -> {
                JsonNode item = schema.path("items");
                List<Object> array = new ArrayList<>();
                array.add(toJava(sampleFromSchema(item, refs, depth + 1)));
                yield mapper.valueToTree(array);
            }
            case "integer", "number" -> mapper.valueToTree(0);
            case "boolean" -> mapper.valueToTree(false);
            default -> mapper.valueToTree("");
        };
    }

    private Object toJava(JsonNode node) {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return "";
        }
        if (node.isObject()) {
            Map<String, Object> map = new LinkedHashMap<>();
            for (Map.Entry<String, JsonNode> entry : node.properties()) {
                map.put(entry.getKey(), toJava(entry.getValue()));
            }
            return map;
        }
        if (node.isArray()) {
            List<Object> list = new ArrayList<>();
            for (JsonNode item : node) {
                list.add(toJava(item));
            }
            return list;
        }
        if (node.isNumber()) {
            return node.numberValue();
        }
        if (node.isBoolean()) {
            return node.booleanValue();
        }
        if (node.isTextual()) {
            return node.stringValue();
        }
        return scalar(node);
    }

    private String placeholder(JsonNode schema) {
        if (schema == null) {
            return "";
        }
        String format = text(schema.path("format"));
        return switch (text(schema.path("type"))) {
            case "integer", "number" -> "0";
            case "boolean" -> "false";
            case "array" -> "[]";
            case "object" -> "{}";
            default -> switch (format) {
                case "email" -> "demo@example.com";
                case "date-time" -> "2026-01-01T00:00:00Z";
                case "date" -> "2026-01-01";
                case "uri", "url" -> "https://example.com";
                default -> "";
            };
        };
    }

    // --------------------------------------------------------------- 通用

    private String firstTag(JsonNode operation) {
        JsonNode tags = operation.path("tags");
        if (tags.isArray() && !tags.isEmpty()) {
            String tag = text(tags.get(0));
            if (!tag.isEmpty()) {
                return Names.dir(tag);
            }
        }
        return "default";
    }

    private String operationId(JsonNode operation, String method, String path) {
        String id = text(operation.path("operationId"));
        if (!id.isEmpty()) {
            return id;
        }
        String fromPath = path.replaceAll("[^A-Za-z0-9]+", "_").replaceAll("^_|_$", "");
        return method + "_" + (fromPath.isEmpty() ? "root" : fromPath);
    }

    private Map<String, JsonNode> refIndex(JsonNode node) {
        Map<String, JsonNode> index = new HashMap<>();
        if (!node.isObject()) {
            return index;
        }
        for (String name : node.propertyNames()) {
            index.put(name, node.path(name));
        }
        return index;
    }

    private JsonNode resolveRef(JsonNode node, Map<String, JsonNode> refs) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return null;
        }
        JsonNode ref = node.path("$ref");
        if (ref.isTextual()) {
            String value = ref.stringValue();
            String name = value.substring(value.lastIndexOf('/') + 1);
            return refs.getOrDefault(name, node);
        }
        return node;
    }

    private List<String> securitySchemes(JsonNode schemes) {
        List<String> result = new ArrayList<>();
        if (!schemes.isObject()) {
            return result;
        }
        for (String name : schemes.propertyNames()) {
            JsonNode scheme = schemes.path(name);
            String type = text(scheme.path("type"));
            if ("http".equals(type) && "bearer".equalsIgnoreCase(text(scheme.path("scheme")))) {
                result.add("bearer");
            } else if ("apiKey".equals(type)) {
                result.add("apiKey");
            } else if ("oauth2".equals(type) || "openIdConnect".equals(type)) {
                result.add("oauth2");
            }
        }
        return result;
    }

    private String firstServer(JsonNode servers) {
        if (!servers.isArray() || servers.isEmpty()) {
            return "";
        }
        JsonNode server = servers.get(0);
        String url = text(server.path("url"));
        if (url.isEmpty()) {
            return "";
        }
        // OpenAPI 允许 "http://localhost:{port}/{basePath}" + variables.default
        JsonNode variables = server.path("variables");
        for (String name : variables.propertyNames()) {
            JsonNode variable = variables.path(name);
            String value = text(variable.path("default"));
            if (value.isEmpty() && variable.path("enum").isArray() && !variable.path("enum").isEmpty()) {
                value = text(variable.path("enum").get(0));
            }
            url = url.replace("{" + name + "}", value);
        }
        return url;
    }

    private String stripSlash(String url) {
        if (url == null || url.isEmpty()) {
            return "";
        }
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    private boolean isIntegerOrBoolean(JsonNode node) {
        return node.isNumber() || node.isBoolean();
    }

    private boolean looksLikeFile(String name, JsonNode sample) {
        String lower = name.toLowerCase(Locale.ROOT);
        return lower.contains("file") || lower.contains("image") || lower.contains("cover");
    }

    private String scalar(JsonNode node) {
        if (node == null || node.isNull() || node.isMissingNode()) {
            return "";
        }
        if (node.isTextual()) {
            return node.stringValue();
        }
        return node.toString();
    }

    /**
     * Jackson 3 的 {@code stringValue()} 是严格方法：对数组 / 对象会抛异常。
     * OpenAPI 3.1 允许 {@code "type": ["string", "null"]}，所以这里必须自己判类型。
     */
    private String text(JsonNode node) {
        if (node == null || node.isMissingNode() || node.isNull() || !node.isTextual()) {
            return "";
        }
        return node.stringValue();
    }
}
