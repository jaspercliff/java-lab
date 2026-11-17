package com.jasper.springdoc.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.headers.Header;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.links.Link;
import io.swagger.v3.oas.models.parameters.Parameter;
import io.swagger.v3.oas.models.parameters.RequestBody;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.annotation.PostConstruct;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springdoc.core.customizers.PropertyCustomizer;
import org.springdoc.core.models.GroupedOpenApi;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.security.Principal;

/**
 * 知识点 B：springdoc 的「程序化」扩展点，注解写不出来的部分都在这里。
 *
 * <ul>
 *   <li>{@link OpenAPI} Bean：手动补 info / components / 扩展，与 {@code @OpenAPIDefinition} 合并</li>
 *   <li>{@link Components}：可复用 parameters / examples / headers / schemas / responses / requestBodies / links</li>
 *   <li>{@link OperationCustomizer}：对每个 operation 做最后加工（统一请求头、x- 扩展）</li>
 *   <li>{@link OpenApiCustomizer}：对整份文档做最后加工</li>
 *   <li>{@link PropertyCustomizer}：对某个属性做最后加工</li>
 *   <li>{@link GroupedOpenApi}：多分组文档 /v3/api-docs/{group}</li>
 *   <li>{@link SpringDocUtils}：全局忽略某些请求包装类型</li>
 * </ul>
 */
@Configuration
public class OpenApiConfiguration {

    /**
     * 构建 OpenAPI 文档的全局信息和可复用组件
     * 组件复用：Controller 里用 {@code @Parameter(ref = "#/components/parameters/traceId")}
     * 直接引用这里定义的公共参数，避免每个方法重复写注解。
     */
    @Bean
    OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("java-lab · springdoc + Swagger UI 知识点大全")
                        .version("1.0.0")
                        .description("由 OpenAPI Bean 补充的信息；静态声明见 @OpenAPIDefinition")
                        .extensions(java.util.Map.of("x-bean-contributed", true)))
                .externalDocs(new ExternalDocumentation()
                        .description("OpenAPI Specification")
                        .url("https://spec.openapis.org/oas/latest.html"))
                .components(new Components()
                        // 1) 可复用的参数（@Parameter(ref=...) / @Parameters 引用）
                        .addParameters("traceId", new Parameter()
                                .in("header")
                                .name("X-Trace-Id")
                                .description("公共链路追踪 ID（来自 components.parameters）")
                                .required(false)
                                .schema(new StringSchema().example("4bf92f3577b34da6a3ce929d0e0e4736")))
                        .addParameters("locale", new Parameter()
                                .in("query")
                                .name("locale")
                                .description("语言，形如 zh-CN")
                                .schema(new StringSchema()._default("zh-CN")))
                        // 2) 可复用的示例（@ExampleObject(ref=...) 或 Swagger UI 展示）
                        .addExamples("bookCreatedJson", new Example()
                                .summary("创建成功的请求示例")
                                .description("POST /api/knowledge/books 的 body")
                                .value("""
                                        {"title":"Springdoc in Action","isbn":"978-1-0000-0000-1","price":88.5,"status":"PUBLISHED","category":1,"tags":["java","openapi"]}
                                        """))
                        // 3) 可复用的响应头
                        .addHeaders("RateLimit", new Header()
                                .description("剩余调用次数")
                                .schema(new Schema<Integer>().type("integer").example(99)))
                        // 4) 可复用的安全方案（与 @SecurityScheme 注解声明的方案并存）
                        .addSecuritySchemes("basicAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")
                                .description("HTTP Basic，演示 Bean 方式声明"))
                        // 5) 程序化 schema / 响应 / 请求体 / link（都可被 $ref 复用）
                        .addSchemas("LabMeta", new ObjectSchema()
                                .description("由 OpenAPI Bean 手工注册的 schema")
                                .addProperty("commit", new StringSchema().example("a1b2c3"))
                                .addProperty("buildTime", new StringSchema().format("date-time")))
                        .addResponses("RateLimited", new ApiResponse()
                                .description("限流响应，可用 @ApiResponse(ref=...) 复用")
                                .content(new Content().addMediaType("application/problem+json",
                                        new MediaType().schema(new Schema<>().type("object")))))
                        .addRequestBodies("BookCreateBody", new RequestBody()
                                .description("可复用请求体")
                                .content(new Content().addMediaType("application/json",
                                        new MediaType().schema(new Schema<>().type("object")))))
                        .addLinks("RelatedBookLink", new Link()
                                .operationId("getBookById")
                                .description("查询关联书籍（components.links）")
                                .addParameter("id", "$response.body#/related/id"))
                        // 6) vendor extension（addExtension 返回 void，链式场景改用 extensions(Map)）
                        .extensions(java.util.Map.of("x-components-note", "所有组件都可被 $ref 复用")))
                .extensions(java.util.Map.of("x-generated-by", "springdoc-openapi 2.10.0 + Swagger UI"));
    }

    /** 每个 operation 生成后都会被这个 Bean 过一遍：适合注入公共请求头、统一打标。
     * links描述一个 API 的响应结果，如何作为另一个 API 的请求参数，从而关联调用下一个 API 也可以描述业务流程
     */
    @Bean
    OperationCustomizer operationCustomizer() {
        return (operation, handlerMethod) -> {
            operation.addParametersItem(new Parameter()
                    .in("header")
                    .name("X-Client-Version")
                    .description("客户端版本（由 OperationCustomizer 全局注入）")
                    .required(false)
                    .schema(new StringSchema().example("1.0.0")));
            // OpenAPI 的 Vendor Extension（厂商扩展） 就是获取 Controller 对应的 Java 方法名称
            operation.addExtension("x-java-handler", handlerMethod.getMethod().getName());
            // 兜底：注解上的 @Link 在某些 springdoc 版本/3.1 输出下不会落到响应里，
            // 这里用 models API 再补一次，保证文档里一定有 links 节点（也演示程序化写法）。
            if (operation.getResponses() != null) {
                ApiResponse ok = operation.getResponses().get("200");
                if (ok != null && "getBookById".equals(operation.getOperationId())) {
                    ok.addLink("DeleteRelatedBook", new Link()
                            .operationId("deleteBook")
                            .description("删除当前书籍关联的书籍")
                            .addParameter("id", "$response.body#/related/id"));
                }
                // 高级 Controller 的 @Link 在 springdoc 2.10 / OpenAPI 3.1 下不落地，程序化补上
                if (ok != null && "advancedGetBookById".equals(operation.getOperationId())) {
                    ok.addLink("RelatedBook", new Link()
                            .operationId("getBookById")
                            .description("关联书籍：用返回的 related.id 继续查询")
                            .addParameter("id", "$response.body#/related/id"));
                }
            }
            return operation;
        };
    }

    /** 整份文档生成后执行：排序标签、追加全局 x- 扩展等。 */
    @Bean
    OpenApiCustomizer openApiCustomizer() {
        return openApi -> {
            // OpenAPI Bean 上的 extensions 会被 @OpenAPIDefinition 覆盖，兜底扩展放这里最可靠
            openApi.addExtension("x-module", "springdoc-swagger-demo");
            openApi.addExtension("x-generated-by", "springdoc-openapi 2.10.0 + Swagger UI");
            if (openApi.getTags() != null) {
                openApi.getTags().forEach(tag -> tag.addExtension("x-tag-defined-globally", true));
            }
        };
    }

    /** 属性级定制：给敏感字段打上自定义扩展，Swagger UI 里可在 schema 描述中看到。 */
    @Bean
    PropertyCustomizer propertyCustomizer() {
        return (schema, annotatedType) -> {
            if ("internalNote".equals(annotatedType.getPropertyName())) {
                schema.addExtension("x-sensitive", true);
            }
            return schema;
        };
    }

    /**
     * 分组文档：默认 profile 不启用，用 {@code --spring.profiles.active=groups} 打开。
     *
     * <p>重要：一旦存在分组，默认的 {@code /v3/api-docs} 会被分组取代，此时必须把
     * {@code springdoc.swagger-ui.urls} 指向 {@code /v3/api-docs/knowledge} 与 {@code /v3/api-docs/advanced}
     * （见 application.yml 注释）。
     */
    @Bean
    @Profile("groups")
    GroupedOpenApi knowledgeGroupedOpenApi() {
        return GroupedOpenApi.builder()
                .group("knowledge")
                .displayName("知识点接口")
                .pathsToMatch("/api/knowledge/**")
                .addOpenApiCustomizer(openApi -> openApi.addExtension("x-group", "knowledge"))
                .build();
    }

    /**
     * 分组文档（groups profile）：高级特性演示单独一组。
     * 启用分组后默认 /v3/api-docs 失效，Swagger UI 需用 springdoc.swagger-ui.urls 同时指向两组。
     */
    @Bean
    @Profile("groups")
    GroupedOpenApi advancedGroupedOpenApi() {
        return GroupedOpenApi.builder()
                .group("advanced")
                .displayName("OpenAPI 高级特性")
                .pathsToMatch("/api/openapi-advanced/**")
                .addOpenApiCustomizer(openApi -> openApi.addExtension("x-group", "advanced"))
                .build();
    }

    /** 告诉 springdoc 某些类型不是请求参数（例如注入的 Principal），不要写进 parameters。 */
    @PostConstruct
    void tuneSpringDoc() {
        SpringDocUtils.getConfig()
                .addRequestWrapperToIgnore(Principal.class);
    }
}
