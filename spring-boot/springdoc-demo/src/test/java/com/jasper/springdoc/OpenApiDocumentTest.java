package com.jasper.springdoc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 集成测试：真正启动 Boot 应用，验证 springdoc 生成的文档确实包含各个知识点，
 * 而不只是「注解写上了」。这也是 springdoc 官方推荐的回归测试方式。
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OpenApiDocumentTest {

    @Autowired
    private TestRestTemplate restTemplate;

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void swaggerUiPageServesTheGeneratedDocument() {
        ResponseEntity<String> ui = restTemplate.getForEntity("/swagger-ui/index.html", String.class);
        assertThat(ui.getStatusCode().value()).isEqualTo(200);
        assertThat(ui.getBody()).contains("Swagger UI", "swagger-ui-bundle.js");

        // 已彻底移除 Scalar UI
        ResponseEntity<String> scalar = restTemplate.getForEntity("/scalar", String.class);
        assertThat(scalar.getStatusCode().value()).isEqualTo(404);

        ResponseEntity<String> yaml = restTemplate.getForEntity("/v3/api-docs.yaml", String.class);
        assertThat(yaml.getStatusCode().value()).isEqualTo(200);
        assertThat(yaml.getBody()).contains("openapi:");
    }

    @Test
    void endpointsReturnCommonApiResponse() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/knowledge/books/1001", String.class);
        assertThat(response.getStatusCode().value()).isEqualTo(200);

        JsonNode body = mapper.readTree(response.getBody());
        assertThat(body.path("code").asInt()).isEqualTo(200);
        assertThat(body.path("message").asText()).isNotBlank();
        assertThat(body.has("data")).isTrue();
        assertThat(body.path("timestamp").asLong()).isPositive();
        // trace starter 已引入，ApiResult 能从 MDC 拿到本次请求的 traceId
        assertThat(body.path("traceId").asText()).isNotBlank();
    }

    @Test
    void apiDocsContainAllKnowledgePoints() throws Exception {
        ResponseEntity<String> response = restTemplate.getForEntity("/v3/api-docs", String.class);
        assertThat(response.getStatusCode().value()).isEqualTo(200);

        JsonNode doc = mapper.readTree(response.getBody());
        JsonNode components = doc.path("components");
        JsonNode paths = doc.path("paths");

        // 全局定义：info / tags / servers / security / extensions
        assertThat(doc.path("info").path("title").asText()).contains("springdoc");
        assertThat(doc.path("servers").size()).isGreaterThanOrEqualTo(2);
        assertThat(doc.path("security").get(0).has("bearerAuth")).isTrue();
        assertThat(doc.has("x-lab")).isTrue();
        assertThat(doc.has("x-generated-by")).isTrue();
        assertThat(doc.path("tags").toString()).contains("00-业务接口", "05-Schema 进阶");

        // components：注解 + Bean 共同构建
        assertThat(components.path("securitySchemes").has("bearerAuth")).isTrue();
        assertThat(components.path("securitySchemes").has("oauth2")).isTrue();
        assertThat(components.path("parameters").has("traceId")).isTrue();
        assertThat(components.path("responses").has("RateLimited")).isTrue();
        assertThat(components.path("links").has("RelatedBookLink")).isTrue();
        assertThat(components.path("schemas").has("BookCreateRequest")).isTrue();
        assertThat(components.path("schemas").has("BookResponse")).isTrue();
        assertThat(doc.toString()).contains("x-sensitive");

        // Bean Validation -> schema 约束；@JsonValue -> enum 使用 code
        JsonNode request = components.path("schemas").path("BookCreateRequest");
        assertThat(request.path("required").toString()).contains("title", "isbn", "price");
        assertThat(request.path("properties").path("title").path("minLength").asInt()).isEqualTo(1);
        assertThat(request.path("properties").path("isbn").has("pattern")).isTrue();
        assertThat(request.path("properties").path("status").path("enum").toString())
                .contains("published", "archived");

        // 两个 Controller 的路径都在
        assertThat(paths.has("/api/knowledge/books")).isTrue();
        assertThat(paths.has("/api/openapi-advanced/parameters")).isTrue();

        // 隐藏端点只隐藏文档，两边都不应出现
        assertThat(paths.has("/api/knowledge/hidden")).isFalse();
        assertThat(paths.has("/api/knowledge/legacy-hidden")).isFalse();
        assertThat(paths.has("/api/openapi-advanced/hidden")).isFalse();
        assertThat(paths.has("/api/openapi-advanced/legacy-hidden")).isFalse();

        // 实战 Controller：只有摘要和少量响应注解，没有高级注解的痕迹
        JsonNode practicalList = paths.path("/api/knowledge/books").path("get");
        assertThat(practicalList.path("operationId").asText()).isEqualTo("listBooks");
        assertThat(practicalList.path("parameters").toString()).contains("keyword", "X-Tenant");
        assertThat(practicalList.path("parameters").toString()).doesNotContain("#/components/parameters/traceId");
        assertThat(practicalList.has("externalDocs")).isFalse();
        assertThat(practicalList.has("x-demo")).isFalse();
        assertThat(paths.path("/api/knowledge/books").path("post").has("callbacks")).isFalse();
        assertThat(paths.path("/api/knowledge/books").path("post").has("x-operation")).isFalse();
        assertThat(paths.path("/api/knowledge/books/{id}").path("get").path("responses")
                .has("429")).isTrue(); // 来自 @RestControllerAdvice

        // 高级 Controller：@Parameters / $ref / @ExampleObject(ref) / @Extension
        JsonNode advancedList = paths.path("/api/openapi-advanced/parameters").path("get");
        assertThat(advancedList.path("operationId").asText()).isEqualTo("advancedParameters");
        assertThat(advancedList.path("parameters").toString()).contains("traceId", "locale", "keywords");
        assertThat(advancedList.path("externalDocs").has("url")).isTrue();
        assertThat(advancedList.has("x-demo")).isTrue();

        // 高级 Controller：@Link（程序化兜底）/ 方法级 @Server / 安全覆盖
        JsonNode advancedGet = paths.path("/api/openapi-advanced/books/{id}").path("get");
        assertThat(advancedGet.path("responses").path("200").path("links").has("RelatedBook")).isTrue();
        assertThat(advancedGet.path("responses").has("429")).isTrue();
        assertThat(advancedGet.path("servers").size()).isEqualTo(1);

        // 高级 Controller：@Callback / @Callbacks / @Encoding
        assertThat(paths.path("/api/openapi-advanced/books").path("post").path("callbacks")
                .has("onBookCreated")).isTrue();
        assertThat(paths.path("/api/openapi-advanced/webhook/verify").path("post").path("callbacks")
                .size()).isEqualTo(2);
        JsonNode uploadBody = paths.path("/api/openapi-advanced/books/{id}/cover").path("post")
                .path("requestBody").path("content").path("multipart/form-data");
        assertThat(uploadBody.path("encoding").has("file")).isTrue();
        assertThat(uploadBody.path("encoding").has("meta")).isTrue();

        // 统一响应后，组合 schema 挂在 data 对应的模型上，而不是响应根节点
        assertThat(components.path("schemas").path("BookPayload").has("oneOf")).isTrue();
        JsonNode composedSchema = components.path("schemas").path("ComposedPayload");
        assertThat(composedSchema.has("anyOf")).isTrue();
        assertThat(composedSchema.path("type").isArray()).isTrue(); // OpenAPI 3.1 types
        assertThat(components.path("schemas").toString()).contains("BookResponse_Summary");
        assertThat(doc.toString()).contains("\"allOf\"");

        assertThat(paths.path("/api/openapi-advanced/enum-codes").path("get").path("responses")
                .path("429").path("$ref").asText()).isEqualTo("#/components/responses/RateLimited");

        // 统一响应契约：所有带 body 的成功响应都指向 ApiResult<...> 变体
        JsonNode template = components.path("schemas").path("ApiResult").path("properties");
        assertThat(template.toString()).contains("code", "message", "data", "timestamp", "traceId");
        int checked = 0;
        for (JsonNode pathItem : paths) {
            for (JsonNode operation : pathItem) {
                if (!operation.isObject() || !operation.has("operationId")) {
                    continue;
                }
                for (JsonNode responseNode : operation.path("responses")) {
                    for (JsonNode mediaType : responseNode.path("content")) {
                        JsonNode schemaRef = mediaType.path("schema").path("$ref");
                        if (schemaRef.isTextual()) {
                            assertThat(schemaRef.asText()).startsWith("#/components/schemas/ApiResult");
                            checked++;
                        }
                    }
                }
            }
        }
        assertThat(checked).isGreaterThan(20);
    }
}
