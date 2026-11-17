package com.jasper.springdoc.controller;

import com.jasper.result.ApiResult;
import com.jasper.springdoc.enums.BookCategory;
import com.jasper.springdoc.enums.BookStatus;
import com.jasper.springdoc.model.*;
import com.jasper.springdoc.service.BookCatalog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.List;

import static com.jasper.result.ApiResult.success;

@RestController
@RequestMapping("/api/knowledge")
@RequiredArgsConstructor
@Tag(name = "00-业务接口", description = "Spring MVC 注解为主，OpenAPI 注解只补摘要与关键响应")
public class KnowledgeController {

    private final BookCatalog catalog;

    /**
     * {@link ParameterObject} —— springdoc 特有注解，把一个 POJO 平坦展开成一组 query 参数，
     * 而不是当成一个 object 类型的请求参数。字段上的 {@code @Parameter} / {@code @Schema} 控制每个参数。
     */
    @GetMapping("/books")
    @Operation(summary = "分页查询书籍")
    public ApiResult<PageResult<BookResponse>> listBooks(
            @Valid @ParameterObject BookQuery query,
            @RequestParam(name = "sort", required = false, defaultValue = "title,asc") String sort,
            @RequestHeader(name = "X-Tenant", required = false) String tenant,
            @CookieValue(name = "session", required = false) String session,
            HttpServletRequest request,
            Principal principal) {
        return success(catalog.page(query.getPage(), query.getSize()));
    }

    @GetMapping("/books/{id}")
    @Operation(summary = "按 ID 查询书籍")
    // @ApiResponse(responseCode = "404", description = "书籍不存在",
    //         content = @Content(schema = @Schema(implementation = ApiResult.class)))
    public ResponseEntity<ApiResult<BookResponse>> getBookById(@PathVariable long id) {
        return ResponseEntity.ok()
                .eTag("v3")
                .body(success(catalog.sample(id)));
    }

    @GetMapping("/search")
    @Operation(summary = "按关键字或分类搜索")
    public ApiResult<List<BookResponse>> searchBooks(
            @RequestParam(name = "keywords", required = false) List<String> keywords,
            @RequestParam(name = "categories", required = false) List<BookCategory> categories) {
        return success(catalog.page(0, 20).getContent());
    }

    @PostMapping(value = "/books", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "创建书籍")
    public ApiResult<BookResponse> createBook(
            @Valid @RequestBody BookCreateRequest request) {
        BookResponse created = catalog.create(request);
   return success(created);
    }

    // @PutMapping("/books/{id}")
    // @Deprecated
    // @Operation(summary = "全量更新（已废弃，请改用 PATCH）", deprecated = true)
    // public ApiResult<BookResponse> replaceBook(@PathVariable long id,
    //                                                                @Valid @RequestBody BookCreateRequest request) {
    //     return success(catalog.create(request));
    // }

    @PatchMapping("/books/{id}/status")
    @Operation(summary = "变更状态")
    public ApiResult<BookResponse> changeStatus(@PathVariable long id,
                                                                    @RequestParam BookStatus status) {
        BookResponse book = catalog.sample(id);
        book.setStatus(status);
        return success(book);
    }

    @DeleteMapping("/books/{id}")
    @Operation(summary = "删除书籍",operationId = "deleteBook")
    public ApiResult<Void> deleteBook(@PathVariable long id) {
        catalog.delete(id);
        return success();
    }

    @PostMapping(value = "/books/{id}/cover", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "上传封面")
    public ApiResult<UploadResult> uploadCover(
            @PathVariable long id,
            @RequestPart("file") MultipartFile file,
            @RequestPart(name = "meta", required = false) String meta) {
        return success(new UploadResult(file.getOriginalFilename(), file.getSize(), file.getContentType()));
    }
    @PostMapping(value = "/echo", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    @Operation(summary = "回声接口")
    public ApiResult<String> echo(@RequestBody(required = false) String body) {
        return success(body == null ? "" : body);
    }

}
