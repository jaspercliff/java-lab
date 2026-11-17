package com.jasper.springdoc.controller;

import com.fasterxml.jackson.annotation.JsonView;
import com.jasper.result.ApiResult;
import com.jasper.springdoc.enums.BookCategory;
import com.jasper.springdoc.enums.BookStatus;
import com.jasper.springdoc.model.*;
import com.jasper.springdoc.service.BookCatalog;
import io.swagger.v3.oas.annotations.*;
import io.swagger.v3.oas.annotations.callbacks.Callback;
import io.swagger.v3.oas.annotations.callbacks.Callbacks;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.enums.ParameterStyle;
import io.swagger.v3.oas.annotations.extensions.Extension;
import io.swagger.v3.oas.annotations.extensions.ExtensionProperty;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.links.Link;
import io.swagger.v3.oas.annotations.links.LinkParameter;
import io.swagger.v3.oas.annotations.media.*;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.servers.ServerVariable;
import io.swagger.v3.oas.annotations.servers.Servers;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.jasper.result.ApiResult.success;

@RestController
@RequestMapping("/api/openapi-advanced")
@RequiredArgsConstructor
// 类级 @SecurityRequirement 会传播到所有方法；方法级的 @SecurityRequirement 可覆盖
@SecurityRequirement(name = "bearerAuth")
public class OpenApiAdvancedController {

    private final BookCatalog catalog;

    // ---------------------------------------------------------------------
    // A01 参数进阶：@Parameters / $ref 复用 / @ParameterObject / array / style / explode / extension
    // ---------------------------------------------------------------------

    @GetMapping("/parameters")
    @Operation(
            summary = "A01 参数进阶：容器、复用、展开与序列化风格",
            operationId = "advancedParameters",
            tags = {"01-查询与参数"},
            description = """
                    1. @Parameters 容器 + @Parameter(ref=...) 复用 components.parameters
                    2. @ParameterObject 把 POJO 展开成多个 query 参数
                    3. @ArraySchema + style/explode/allowReserved/allowEmptyValue/deprecated
                    4. @Operation(externalDocs/extensions) 与 @ExampleObject(ref=...)
                    """,
            externalDocs = @ExternalDocumentation(description = "OpenAPI Parameter Object",
                    url = "https://spec.openapis.org/oas/v3.0.3.html#parameter-object"),
            extensions = @Extension(name = "x-demo", properties = {
                    @ExtensionProperty(name = "topic", value = "parameters"),
                    @ExtensionProperty(name = "paged", value = "true", parseValue = true)
            })
    )
    @Parameters({
            @Parameter(ref = "#/components/parameters/traceId"),
            @Parameter(ref = "#/components/parameters/locale"),
            @Parameter(name = "keywords", in = ParameterIn.QUERY, description = "逗号分隔关键字",
                    style = ParameterStyle.FORM, explode = Explode.FALSE, allowReserved = true,
                    array = @ArraySchema(schema = @Schema(type = "string"),
                            minItems = 1, maxItems = 10, uniqueItems = true),
                    examples = @ExampleObject(name = "twoKeys", summary = "两个关键字", value = "java,openapi")),
            @Parameter(name = "legacyToken", in = ParameterIn.QUERY, description = "废弃参数",
                    deprecated = true, allowEmptyValue = true)
    })
    public ApiResult<PageResult<BookResponse>> advancedParameters(
            @ParameterObject @Valid BookQuery query,
            @RequestParam(name = "keywords", required = false) List<String> keywords,
            @RequestParam(name = "legacyToken", required = false) String legacyToken,
            @Parameter(hidden = true) HttpServletRequest request) {
        return success(catalog.page(query.getPage(), query.getSize()));
    }

    // ---------------------------------------------------------------------
    // A02 响应进阶：@ApiResponses / @Link / @Header / @Extension / 方法级 @Server / 安全覆盖
    // ---------------------------------------------------------------------

    @GetMapping("/books/{id}")
    @Operation(
            summary = "A02 响应链接与服务器覆盖",
            operationId = "advancedGetBookById",
            tags = {"03-响应与链接"},
            servers = @Server(url = "https://{env}.api.example.com", description = "按环境路由",
                    variables = @ServerVariable(name = "env", defaultValue = "dev",
                            allowableValues = {"dev", "prod"})),
            parameters = @Parameter(name = "id", in = ParameterIn.PATH, required = true,
                    description = "书籍主键", example = "1001",
                    schema = @Schema(type = "integer", format = "int64", minimum = "1"))
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功", useReturnTypeSchema = true,
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(name = "ok", summary = "一条书籍",
                                    value = "{\"code\":200,\"message\":\"操作成功\",\"data\":{\"id\":1001,\"title\":\"Springdoc in Action\"}}")),
                    headers = @Header(name = "ETag", description = "资源版本",
                            schema = @Schema(type = "string", example = "\"v3\"")),
                    links = @Link(name = "UpdateBook", operationId = "replaceBook",
                            description = "用返回 id 直接调用更新接口",
                            parameters = @LinkParameter(name = "id", expression = "$request.path.id")),
                    extensions = @Extension(name = "x-response", properties =
                    @ExtensionProperty(name = "linked", value = "true", parseValue = true))),
            @ApiResponse(responseCode = "404", description = "书籍不存在",
                    content = @Content(schema = @Schema(implementation = ApiResult.class))),
            @ApiResponse(responseCode = "410", description = "已归档不可用", content = @Content())
    })
    @SecurityRequirement(name = "apiKeyAuth")
    public ResponseEntity<ApiResult<BookResponse>> advancedGetBookById(@PathVariable long id) {
        return ResponseEntity.ok().eTag("v3").body(success(catalog.sample(id)));
    }

    // ---------------------------------------------------------------------
    // A03 请求体进阶：swagger @RequestBody / 多 mediaType / 多示例 / @Extension / @Callback
    // ---------------------------------------------------------------------

    @PostMapping(value = "/books", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "A03 请求体与单回调",
            operationId = "advancedCreateBook",
            tags = {"02-请求体与校验"},
            description = "swagger 的 @RequestBody 用于补充文档，不与 Spring 的 @RequestBody 冲突",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "创建书籍的完整载荷",
                    required = true,
                    content = {
                            @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    schema = @Schema(implementation = BookCreateRequest.class),
                                    examples = {
                                            @ExampleObject(name = "full", summary = "完整请求",
                                                    description = "带全部可选字段",
                                                    value = """
                                                            {"title":"Springdoc in Action","isbn":"978-1-0000-0000-1","price":88.50,"status":"published","category":1,"tags":["java"],"contactEmail":"dev@example.com","stock":42}
                                                            """),
                                            @ExampleObject(name = "minimal", summary = "最小请求",
                                                    ref = "#/components/examples/bookCreatedJson")
                                    },
                                    extensions = @Extension(name = "x-body", properties =
                                    @ExtensionProperty(name = "validated", value = "true", parseValue = true))),
                            @Content(mediaType = MediaType.TEXT_PLAIN_VALUE,
                                    schema = @Schema(type = "string", example = "演示第二种 mediaType"))
                    }),
            extensions = @Extension(name = "x-operation", properties = {
                    @ExtensionProperty(name = "idempotent", value = "false", parseValue = true),
                    @ExtensionProperty(name = "rateLimit", value = "100/min")
            })
    )
    @SecurityRequirement(name = "oauth2", scopes = {"book:write"})
    @Callback(
            name = "onBookCreated",
            callbackUrlExpression = "{$request.header#/X-Callback-Url}",
            operation = @Operation(
                    method = "POST",
                    operationId = "onBookCreatedCallback",
                    summary = "创建成功后服务端反向回调业务方",
                    requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                            required = true,
                            content = @Content(schema = @Schema(implementation = BookEventCallback.class))),
                    responses = @ApiResponse(responseCode = "200", description = "业务方已接收"))
    )
    public ResponseEntity<ApiResult<BookResponse>> advancedCreateBook(
            @Valid @RequestBody BookCreateRequest request,
            @Parameter(description = "回调地址", example = "https://client.example.com/hooks/book")
            @RequestHeader(name = "X-Callback-Url", required = false) String callbackUrl) {
        BookResponse created = catalog.create(request);
        return ResponseEntity.created(URI.create("/api/openapi-advanced/books/" + created.getId()))
                .body(success(created));
    }

    // ---------------------------------------------------------------------
    // A04 @Callbacks 容器：一个接口挂多个回调操作
    // ---------------------------------------------------------------------

    @PostMapping("/webhook/verify")
    @Operation(summary = "A04 @Callbacks 容器", operationId = "advancedVerifyWebhook",
            tags = {"02-请求体与校验"},
            description = "回调地址由请求头给出，文档里会生成 callbacks 节点")
    @Callbacks({
            @Callback(name = "onCreated", callbackUrlExpression = "{$request.header#/X-Callback-Url}",
                    operation = @Operation(method = "POST", summary = "创建事件",
                            operationId = "onAdvancedCreatedEvent",
                            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                    content = @Content(schema = @Schema(implementation = BookEventCallback.class))),
                            responses = @ApiResponse(responseCode = "200", description = "已接收"))),
            @Callback(name = "onDeleted", callbackUrlExpression = "{$request.header#/X-Callback-Url}",
                    operation = @Operation(method = "POST", summary = "删除事件",
                            operationId = "onAdvancedDeletedEvent",
                            responses = @ApiResponse(responseCode = "200", description = "已接收")))
    })
    @ApiResponse(responseCode = "202", description = "已接受", useReturnTypeSchema = true)
    public ResponseEntity<ApiResult<Void>> advancedVerifyWebhook(
            @Parameter(description = "回调地址")
            @RequestHeader(name = "X-Callback-Url") String callbackUrl,
            @RequestBody BookEventCallback event) {
        // 演示用：不做真实网络调用
        return ResponseEntity.accepted().body(success());
    }

    // ---------------------------------------------------------------------
    // A05 multipart：@Encoding / @SchemaProperty / format=binary
    // ---------------------------------------------------------------------

    @PostMapping(value = "/books/{id}/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "A05 multipart 编码", operationId = "advancedUploadCover",
            tags = {"02-请求体与校验"},
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "multipart/form-data，file 为二进制，meta 为 JSON 文本",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(type = "object", requiredProperties = {"file"},
                                    extensions = @Extension(name = "x-multipart", properties =
                                    @ExtensionProperty(name = "maxSize", value = "5MB"))),
                            schemaProperties = {
                                    @SchemaProperty(name = "file",
                                            schema = @Schema(type = "string", format = "binary",
                                                    description = "图片二进制")),
                                    @SchemaProperty(name = "meta",
                                            schema = @Schema(type = "string", description = "备注 JSON"))
                            },
                            encoding = {
                                    @Encoding(name = "file", contentType = "image/png, image/jpeg",
                                            headers = @Header(name = "X-File-Source", description = "文件来源",
                                                    schema = @Schema(type = "string"))),
                                    @Encoding(name = "meta", contentType = MediaType.APPLICATION_JSON_VALUE)
                            })))
    @ApiResponse(responseCode = "200", description = "上传成功", useReturnTypeSchema = true)
    public ApiResult<UploadResult> advancedUploadCover(
            @PathVariable long id,
            @Parameter(description = "封面图片", required = true,
                    content = @Content(mediaType = MediaType.IMAGE_PNG_VALUE,
                            schema = @Schema(type = "string", format = "binary")))
            @RequestPart("file") MultipartFile file,
            @Parameter(description = "备注")
            @RequestPart(name = "meta", required = false) String meta) {
        return success(new UploadResult(file.getOriginalFilename(), file.getSize(), file.getContentType()));
    }

    // ---------------------------------------------------------------------
    // A06 @SecurityRequirements（OR）/ @Tags 容器 / @Servers 列表
    // ---------------------------------------------------------------------

    @GetMapping("/admin")
    @Tags({
            @Tag(name = "03-响应与链接"),
            @Tag(name = "00-总览", description = "演示 @Tags 容器给单个操作打多个标签")
    })
    @Operation(summary = "A06 多安全方案（Bearer 或 API Key）", operationId = "advancedAdminOverview")
    @SecurityRequirements({
            @SecurityRequirement(name = "bearerAuth"),
            @SecurityRequirement(name = "apiKeyAuth")
    })
    @Servers({
            @Server(url = "https://admin.example.com", description = "管理面专用域名"),
            @Server(url = "http://localhost:{port}", description = "本地调试",
                    variables = @ServerVariable(name = "port", defaultValue = "18090"))
    })
    @ApiResponse(responseCode = "200", description = "OK", useReturnTypeSchema = true)
    public ApiResult<Map<String, String>> advancedAdminOverview() {
        return success(Map.of("scope", "admin"));
    }

    // ---------------------------------------------------------------------
    // A07 隐藏：@Hidden 与 @Operation(hidden=true)
    // ---------------------------------------------------------------------

    @Hidden
    @GetMapping("/hidden")
    public ApiResult<Map<String, String>> advancedHidden() {
        return success(Map.of("visible", "false"));
    }

    @Operation(hidden = true, summary = "不会出现在文档里", tags = {"03-响应与链接"})
    @GetMapping("/legacy-hidden")
    public ApiResult<Map<String, String>> advancedLegacyHidden() {
        return success(Map.of("legacy", "true"));
    }

    // ---------------------------------------------------------------------
    // A08 @JsonView / ignoreJsonView：同一 DTO 生成不同 schema
    // ---------------------------------------------------------------------

    @GetMapping("/books/{id}/summary")
    @JsonView(Views.Summary.class)
    @Operation(summary = "A08 @JsonView 摘要视图", operationId = "advancedBookSummary",
            tags = {"05-Schema 进阶"},
            description = "@JsonView(Views.Summary.class) 会过滤掉 Admin 专属字段")
    @ApiResponse(responseCode = "200", description = "摘要", useReturnTypeSchema = true)
    public ApiResult<BookResponse> advancedBookSummary(@PathVariable long id) {
        return success(catalog.sample(id));
    }

    @GetMapping("/books/{id}/full")
    @JsonView(Views.Admin.class)
    @Operation(summary = "A08 ignoreJsonView 忽略视图过滤", operationId = "advancedBookFull",
            tags = {"05-Schema 进阶"},
            ignoreJsonView = true)
    @ApiResponse(responseCode = "200", description = "完整对象", useReturnTypeSchema = true)
    public ApiResult<BookResponse> advancedBookFull(@PathVariable long id) {
        return success(catalog.sample(id));
    }

    // ---------------------------------------------------------------------
    // A09 @Schema(oneOf) + discriminator（挂在 BookPayload 模型上，作为 ApiResult.data）
    // ---------------------------------------------------------------------

    @GetMapping("/payload")
    @Operation(summary = "A09 @Schema(oneOf) 多态载荷", operationId = "advancedPayloadDemo",
            tags = {"05-Schema 进阶"},
            description = "data 为 BookPayload，其模型上声明 oneOf + discriminator；配合 one-of-as-ref 抽成组件")
    @ApiResponse(responseCode = "200", description = "多态对象", useReturnTypeSchema = true,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = {
                            @ExampleObject(name = "text", summary = "文本",
                                    value = "{\"code\":200,\"message\":\"操作成功\",\"data\":{\"kind\":\"text\",\"text\":\"hello\"}}"),
                            @ExampleObject(name = "image", summary = "图片",
                                    value = "{\"code\":200,\"message\":\"操作成功\",\"data\":{\"kind\":\"image\",\"url\":\"https://cdn/1.png\"}}")
                    }))
    public ApiResult<BookPayload> advancedPayloadDemo() {
        TextPayload payload = new TextPayload();
        payload.setKind("text");
        payload.setText("hello");
        return success(payload);
    }

    // ---------------------------------------------------------------------
    // A10 @Schema(anyOf) + OpenAPI 3.1 的 types 多类型（挂在 ComposedPayload 模型上）
    // ---------------------------------------------------------------------

    @GetMapping("/composed")
    @Operation(summary = "A10 @Schema(anyOf) + 3.1 types", operationId = "advancedComposedSchema",
            tags = {"05-Schema 进阶"},
            description = "data 为 ComposedPayload：anyOf=满足任一 / oneOf=恰好一个（A09）/ allOf=同时满足（model 类）")
    @ApiResponse(responseCode = "200", description = "组合对象", useReturnTypeSchema = true,
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    extensions = @Extension(name = "x-composed", properties =
                    @ExtensionProperty(name = "specVersion", value = "3.1"))))
    public ApiResult<ComposedPayload> advancedComposedSchema() {
        return success(new ComposedPayload());
    }

    // ---------------------------------------------------------------------
    // A11 枚举码表 + @ApiResponse(ref=...) 复用 components.responses
    // ---------------------------------------------------------------------

    @GetMapping("/enum-codes")
    @Operation(summary = "A11 枚举码表与响应复用", operationId = "advancedEnumCodes",
            tags = {"05-Schema 进阶"})
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "枚举码表", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "429", description = "复用 components.responses 的响应定义",
                    ref = "#/components/responses/RateLimited")
    })
    public ApiResult<Map<String, List<String>>> advancedEnumCodes() {
        return success(Map.of(
                "status", Arrays.stream(BookStatus.values()).map(BookStatus::getCode).toList(),
                "category", Arrays.stream(BookCategory.values()).map(c -> String.valueOf(c.getCode())).toList()
        ));
    }
}
