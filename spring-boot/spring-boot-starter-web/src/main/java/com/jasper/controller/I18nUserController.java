package com.jasper.controller;

import com.jasper.dto.UserCreateRequest;
import com.jasper.result.ApiResult;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("user")
public class I18nUserController {

    private final MessageSource messageSource;

    /**
     * xh GET :18080/user Accept-Language:zh-CN
     * <br>
     * xh GET :18080/user Accept-Language:en-US
     */
    @GetMapping
    public ApiResult<String> getMessage() {

        String message = messageSource.getMessage(
                "user.create.success",
                null,
                LocaleContextHolder.getLocale()
        );

        return ApiResult.success(message);
    }

    @PostMapping
    public ApiResult<String> createUser(@Valid @RequestBody UserCreateRequest request) {
        return ApiResult.success("ok");
    }



    // @GetMapping("globalEx")
    // public ApiResult<String> getGlobalEx(){
    //     throw new BusinessException("")
    // }

    // @PostMapping
    // public ApiResult<String> createUser(CreateUser request) {
    //
    //
    //     return ApiResult.success(message);
    // }

}
