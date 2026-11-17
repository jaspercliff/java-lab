package com.jasper.springdoc;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.security.OAuthFlow;
import io.swagger.v3.oas.annotations.security.OAuthFlows;
import io.swagger.v3.oas.annotations.security.OAuthScope;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.servers.ServerVariable;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 知识点 A：文档「全局信息」——全部由 swagger-annotations 的注解声明，springdoc 启动时扫描合并。
 *
 * <ul>
 *   <li>{@link OpenAPIDefinition}：info / tags / servers / security / externalDocs / extensions</li>
 *   <li>{@link Info} + {@link Contact} + {@link License}：标题、版本、summary、条款、联系人、许可证</li>
 *   <li>{@link Server} + {@link ServerVariable}：服务器地址与可替换变量</li>
 *   <li>{@link Tag}：全局 tag 定义与顺序（Swagger UI 侧边栏顺序以此为准）</li>
 *   <li>{@link SecurityScheme}：bearer / apiKey / oauth2 三种安全方案</li>
 *   <li>{@link ExternalDocumentation}：外部文档链接</li>
 *   <li>{@link Extension} / {@link ExtensionProperty}：x- 厂商扩展（parseValue 可把字符串解析成 JSON）</li>
 * </ul>
 *
 * <p>与 {@code OpenAPI} Bean 的关系：注解负责静态声明，Bean 负责程序化补充 components，
 * springdoc 会合并两者；实测 info / 根级 extensions 以注解为准，需要保证落地的全局扩展
 * 建议统一放到 {@code OpenApiCustomizer}（见 OpenApiConfiguration）。
 */
@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "java-lab · springdoc + Swagger UI 知识点大全",
                version = "1.0.0",
                summary = " Springdoc / OpenAPI 文档全部知识点",
                description = """
                        springdoc-openapi 负责从 Spring MVC 注解生成 OpenAPI 描述，Swagger UI 只负责渲染这份描述。
                        """,
                termsOfService = "https://example.com/terms",
                contact = @Contact(name = "jasper", url = "https://github.com/jaspercliff", email = "dev@example.com"),
                license = @License(name = "MIT", identifier = "MIT", url = "https://opensource.org/licenses/MIT"),
                extensions = @Extension(name = "x-info", properties = {
                        @ExtensionProperty(name = "owner", value = "java-lab"),
                        @ExtensionProperty(name = "generated", value = "true", parseValue = true)
                })
        ),
        tags = {
                @Tag(name = "00-总览", description = "演示 @Tags 容器给单个操作打多个标签"),
                @Tag(name = "01-查询与参数", description = "路径/查询/请求头/Cookie/矩阵变量/参数对象",
                        externalDocs = @ExternalDocumentation(description = "OpenAPI Parameter Object",
                                url = "https://spec.openapis.org/oas/v3.0.3.html#parameter-object")),
                @Tag(name = "02-请求体与校验", description = "@RequestBody / @Content / @ExampleObject / Bean Validation"),
                @Tag(name = "03-响应与链接", description = "@ApiResponse / @Header / @Link / ProblemDetail / 文件下载"),
                @Tag(name = "04-安全与回调", description = "@SecurityRequirement / @Callback / @Hidden / deprecated"),
                @Tag(name = "05-Schema 进阶", description = "泛型 / 多态 / oneOf / anyOf / allOf / @JsonView / 枚举")
        },
        servers = {
                @Server(
                        url = "http://localhost:{port}/{basePath}",
                        description = "本地开发环境",
                        variables = {
                                @ServerVariable(name = "port", defaultValue = "18090",
                                        allowableValues = {"18090", "8080"}, description = "服务端口"),
                                @ServerVariable(name = "basePath", defaultValue = "", description = "context-path")
                        }
                ),
                @Server(url = "https://api.example.com", description = "生产环境")
        },
        security = @SecurityRequirement(name = "bearerAuth"),
        externalDocs = @ExternalDocumentation(description = "springdoc 官方文档", url = "https://springdoc.org/"),
        extensions = {
                @Extension(name = "x-lab", properties = {
                        @ExtensionProperty(name = "module", value = "springdoc-swagger-demo"),
                        @ExtensionProperty(name = "openapiExtensionsSupported", value = "true", parseValue = true)
                })
        }
)
@SecuritySchemes({
        @SecurityScheme(
                name = "bearerAuth",
                type = SecuritySchemeType.HTTP,
                scheme = "bearer",
                bearerFormat = "JWT",
                description = "JWT Bearer Token，Swagger UI 中可直接点 Authorize 填入"
        ),
        @SecurityScheme(
                name = "apiKeyAuth",
                type = SecuritySchemeType.APIKEY,
                in = SecuritySchemeIn.HEADER,
                paramName = "X-API-Key",
                description = "API Key 放在请求头"
        ),
        @SecurityScheme(
                name = "oauth2",
                type = SecuritySchemeType.OAUTH2,
                description = "OAuth2 授权码模式",
                flows = @OAuthFlows(
                        authorizationCode = @OAuthFlow(
                                authorizationUrl = "https://idp.example.com/oauth2/authorize",
                                tokenUrl = "https://idp.example.com/oauth2/token",
                                refreshUrl = "https://idp.example.com/oauth2/refresh",
                                scopes = {
                                        @OAuthScope(name = "book:read", description = "读取书籍"),
                                        @OAuthScope(name = "book:write", description = "修改书籍")
                                }
                        )
                )
        )
})
public class SpringdocSwaggerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringdocSwaggerApplication.class, args);
    }
}
