package com.jasper.controller;

import com.jasper.anno.Auth;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("interceptor")
public class InterceptorController {

    @Auth
    @GetMapping
    public String enable() {
        return "enabled";
    }
}
