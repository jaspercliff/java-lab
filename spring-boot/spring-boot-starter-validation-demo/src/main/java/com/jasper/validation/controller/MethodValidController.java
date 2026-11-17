package com.jasper.validation.controller;

import com.jasper.result.ApiResult;
import com.jasper.validation.constraint.FatalPayload;
import com.jasper.validation.constraint.anno.Phone;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/validate")
@RequiredArgsConstructor
@Validated
@Tag(name = "validation test", description = "validation test")
public class MethodValidController {

    @PostMapping("/users")
    public ApiResult<String> createUser(@Phone(nullable = false,payload = FatalPayload.class) String phone) {
        return ApiResult.success(phone);
    }
}
