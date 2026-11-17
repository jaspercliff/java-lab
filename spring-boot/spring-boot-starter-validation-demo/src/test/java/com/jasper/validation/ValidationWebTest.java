package com.jasper.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jasper.validation.enums.GenderEnum;
import com.jasper.validation.model.Address;
import com.jasper.validation.model.UserCreateRequest;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

/** MockMvc 集成测试：验证三种校验异常都被转成 400 + common 的 ApiResult 统一结构。 */
@SpringBootTest
@AutoConfigureMockMvc
class ValidationWebTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void bodyValidationReturnsFieldErrors() throws Exception {
        UserCreateRequest req = validRequest();
        req.setPhone("123");
        req.setEmail("bad");
        req.setAddress(new Address("", "杭州"));

        mockMvc.perform(post("/api/validation/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("VALIDATION_BODY: 请求参数校验失败"))
                .andExpect(jsonPath("$.data[*].field").value(org.hamcrest.Matchers.hasItems(
                        "phone", "email", "address.province")));
    }

    @Test
    void bodyValidationWithEnglishLocaleUsesEnglishMessage() throws Exception {
        UserCreateRequest req = validRequest();
        req.setPhone("123");

        mockMvc.perform(post("/api/validation/users")
                        .header("Accept-Language", "en")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data[?(@.field=='phone')].message")
                        .value(org.hamcrest.Matchers.hasItem("invalid phone number")));
    }

    @Test
    void chineseLocaleGetsChineseMessage() throws Exception {
        UserCreateRequest req = validRequest();
        req.setPhone("123");

        mockMvc.perform(post("/api/validation/users")
                        .header("Accept-Language", "zh-CN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data[?(@.field=='phone')].message")
                        .value(org.hamcrest.Matchers.hasItem("手机号格式不正确")));
    }

    @Test
    void validBodyPassesWithApiResult() throws Exception {
        mockMvc.perform(post("/api/validation/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("newuser"))
                .andExpect(jsonPath("$.traceId").exists());
    }

    @Test
    void createGroupRejectsTakenUsername() throws Exception {
        UserCreateRequest req = validRequest();
        req.setUsername("admin");

        mockMvc.perform(post("/api/validation/users/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data[0].field").value("username"));
    }

    @Test
    void updateGroupRejectsMissingId() throws Exception {
        mockMvc.perform(post("/api/validation/users/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.data[0].field").value("id"));
    }

    @Test
    void requestParamConstraintReturnsParamErrorCode() throws Exception {
        mockMvc.perform(get("/api/validation/query").param("keyword", "  "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("VALIDATION_PARAM: 方法参数校验失败"));
    }

    @Test
    void pathVariableConstraintReturnsParamErrorCode() throws Exception {
        mockMvc.perform(get("/api/validation/users/0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("VALIDATION_PARAM: 方法参数校验失败"));
    }

    @Test
    void serviceMethodValidationReturnsMethodErrorCode() throws Exception {
        mockMvc.perform(get("/api/validation/method").param("keyword", ""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("VALIDATION_METHOD: 方法入参校验失败"))
                .andExpect(jsonPath("$.data[0].field").value("keyword"));
    }

    @Test
    void errorEnvelopeIsApiResult() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/validation/query").param("keyword", ""))
                .andExpect(status().isBadRequest())
                .andReturn();
        JsonNode json = objectMapper.readTree(result.getResponse().getContentAsString());
        assertThat(json.get("code").asInt()).isEqualTo(400);
        assertThat(json.get("timestamp").isNumber()).isTrue();
        assertThat(json.get("message").asText()).contains("VALIDATION_PARAM");
        assertThat(json.get("data").isArray()).isTrue();
        // ApiResult 的 traceId 来自 MDC，由 common-trace-spring-boot-starter 写入
        assertThat(json.get("traceId").asText()).isNotBlank();
    }

    private static UserCreateRequest validRequest() {
        Address address = new Address("浙江省", "杭州市");
        return UserCreateRequest.builder()
                .username("newuser")
                .phone("13800138000")
                .email("a@b.com")
                .password("abc12345")
                .gender(GenderEnum.MALE) // @JsonValue 整数码，Jackson 序列化为 1
                .age(25)
                .birthday(LocalDate.of(2000, 1, 1))
                .address(address)
                .shippingAddresses(List.of(address))
                .activeFrom(LocalDate.of(2026, 1, 1))
                .activeTo(LocalDate.of(2026, 12, 31))
                .agreed(true)
                .build();
    }
}
