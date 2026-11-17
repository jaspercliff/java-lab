package com.jasper.validation.controller;

import com.jasper.result.ApiResult;
import com.jasper.validation.constraint.group.CreateGroup;
import com.jasper.validation.constraint.group.OrderedChecks;
import com.jasper.validation.constraint.group.UpdateGroup;
import com.jasper.validation.model.UserCreateRequest;
import com.jasper.validation.service.MethodValidatedService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.groups.Default;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Controller 层校验的三种写法（对比记忆）：
 *
 * <ol>
 *   <li>类上不标 {@link Validated} + {@code @Valid @RequestBody}：
 *       校验请求体，失败抛 {@code MethodArgumentNotValidException}；</li>
 *   <li>类上标 {@code @Validated} + 方法参数上写约束：
 *       校验 @RequestParam/@PathVariable，失败抛 {@code ConstraintViolationException}；</li>
 *   <li>类上不标 {@link Validated} + 方法参数上直接写约束（Spring 6.1+）：
 *       走 MVC 内置方法校验，失败抛 {@code HandlerMethodValidationException}。</li>
 * </ol>
 *
 * <p>按工程约定，所有成功响应统一用 common 的 {@link ApiResult}（含 traceId）；
 * 失败响应由 {@code GlobalExceptionHandler} 统一包装成 ApiResult。
 */
@RestController
@RequestMapping("/api/validation")
@RequiredArgsConstructor
@Tag(name = "validation test", description = "validation test")
public class ValidationController {

    private final MethodValidatedService methodValidatedService;

    /** ① 请求体校验：@Valid 触发 DTO 上全部 Default 组约束（含 @Valid 级联、类级约束）。这里不校验createGroup */
    @PostMapping("/users")
    public ApiResult<UserCreateRequest> createUser(@Valid @RequestBody UserCreateRequest request) {
        return ApiResult.success(request);
    }

    /** 分组：只校验 CreateGroup 组（用户名非空/长度/唯一），Default 组的手机号等不校验。
     * 自定义校验 UniqueUsername
     * */
    @PostMapping("/users/create")
    public ApiResult<String> createGroup(@Validated(CreateGroup.class) @RequestBody UserCreateRequest request) {
        return ApiResult.success("create group ok: " + request.getUsername());
    }

    /**俩个分组都校验*/
    @PostMapping("/users/create1")
    public ApiResult<String> createGroup1(
            @Validated({
                    Default.class,
                    CreateGroup.class
            })
            @RequestBody UserCreateRequest request) {

        return ApiResult.success("create group ok: " + request.getUsername());
    }

    /** 分组：只校验 UpdateGroup 组（id 非空）。 */
    @PostMapping("/users/update")
    public ApiResult<String> updateGroup(@Validated(UpdateGroup.class) @RequestBody UserCreateRequest request) {
        return ApiResult.success("update group ok: " + request.getId());
    }

    /** 组序列：先 CreateGroup后Default ，前一组有错就不执行后一组。 */
    @PostMapping("/users/ordered")
    public ApiResult<String> ordered(@Validated(OrderedChecks.class) @RequestBody UserCreateRequest request) {
        return ApiResult.success("ordered ok");
    }

    /**
     * ③ Spring 6.1+ 内置方法参数校验：类上无 @Validated，约束直接写在 @RequestParam 上，
     * 失败抛 HandlerMethodValidationException。参数名要保留（需 -parameters，
     * 由 org.springframework.boot Gradle 插件自动加到 JavaCompile，无需手写）。
     */
    @GetMapping("/query")
    public ApiResult<String> query(@RequestParam(required = false) @NotBlank(message = "关键字不能为空") String keyword,
                                   @RequestParam(defaultValue = "1") @Min(value = 1, message = "页数最小为 1") int page) {
        return ApiResult.success("keyword=" + keyword + ", page=" + page);
    }

    /** @PathVariable 上的约束同样由 Spring 6.1+ 内置方法校验处理。 */
    @GetMapping("/users/{id}")
    public ApiResult<Long> getUser(@PathVariable @Min(value = 1, message = "用户 id 必须大于 0") Long id) {
        return ApiResult.success(id);
    }

    /** 调 Service 的方法级校验，失败抛 ConstraintViolationException。 */
    @GetMapping("/method")
    public ApiResult<String> methodValidation(@RequestParam(required = false) String keyword,
                                              @RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int size) {
        return ApiResult.success(methodValidatedService.query(keyword, page, size));
    }
}
