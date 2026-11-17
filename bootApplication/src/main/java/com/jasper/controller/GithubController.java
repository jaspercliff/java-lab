package com.jasper.controller;

import com.jasper.result.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("github")
@RequiredArgsConstructor
public class GithubController {
    private final RestClient restClient;

    @GetMapping("person")
    public ApiResult<String> person() {
        String result = restClient
                .get()
                .uri("https://api.github.com/users/jaspercliff")
                .retrieve()
                .body(String.class);
        return ApiResult.success(result);
    }
}
