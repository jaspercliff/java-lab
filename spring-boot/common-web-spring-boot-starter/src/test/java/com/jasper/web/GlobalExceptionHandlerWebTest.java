package com.jasper.web;

import com.jasper.exception.BusinessException;
import com.jasper.security.core.SensitiveFieldMatcher;
import com.jasper.web.config.properties.WebProperties;
import com.jasper.web.exception.GlobalExceptionHandler;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Locale;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** 从 HTTP 层验证：异常经 advice 处理后确实是 ApiResult 结构 + 对应状态码。 */
@ExtendWith(ChineseLocaleExtension.class)
class GlobalExceptionHandlerWebTest {

    private final MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new BoomController())
            .setControllerAdvice(new GlobalExceptionHandler(new WebProperties(true, false),
                    new SensitiveFieldMatcher(List.of("password", "token")), TestMessages.source()))
            .build();

    @Test
    void businessExceptionReturnsBadRequestApiResult() throws Exception {
        mockMvc.perform(get("/boom"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("ORDER_001"))
                .andExpect(jsonPath("$.message").value("库存不足"))
                .andExpect(jsonPath("$.data.code").value("ORDER_001"));
    }

    @Test
    void bodyValidationReturnsViolationsInData() throws Exception {
        mockMvc.perform(post("/validate")
                        .contentType("application/json")
                        .content("{\"name\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("400"))
                .andExpect(jsonPath("$.data[0].field").value("name"));
    }

    @Test
    void unexpectedExceptionReturnsGenericMessageAnd500() throws Exception {
        mockMvc.perform(get("/oops").locale(Locale.SIMPLIFIED_CHINESE))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value("500"))
                .andExpect(jsonPath("$.message").value("服务器内部错误"))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.not(
                        org.hamcrest.Matchers.containsString("secret"))));
    }

    @RestController
    static class BoomController {

        @GetMapping("/boom")
        String boom() {
            throw new BusinessException("ORDER_001", "库存不足");
        }

        @GetMapping("/oops")
        String oops() {
            throw new IllegalStateException("内部细节 secret");
        }

        @PostMapping("/validate")
        String validate(@Valid @RequestBody QueryBody body) {
            return "ok";
        }
    }

    public static class QueryBody {
        @NotBlank(message = "名称不能为空")
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
