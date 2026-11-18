package com.jasper.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jasper.result.ApiResult;
import com.jasper.web.advice.ApiResultResponseAdvice;
import com.jasper.web.annotation.IgnoreApiResult;
import com.jasper.web.config.properties.ResponseProperties;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

class ApiResultResponseAdviceTest {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ApiResultResponseAdvice advice = new ApiResultResponseAdvice(objectProvider(),
            new ResponseProperties(true));

    @Test
    void wrapsObjectIntoApiResult() throws Exception {
        assertThat(advice.supports(param("pojo"), MappingJackson2HttpMessageConverter.class)).isTrue();

        Object result = advice.beforeBodyWrite("data", param("pojo"), MediaType.APPLICATION_JSON,
                MappingJackson2HttpMessageConverter.class, request(), response());

        assertThat(result).isInstanceOf(ApiResult.class);
        assertThat(((ApiResult<?>) result).code()).isEqualTo("200");
        assertThat(((ApiResult<?>) result).data()).isEqualTo("data");
    }

    @Test
    void serializesStringResponseAsJson() throws Exception {
        String result = (String) advice.beforeBodyWrite("hello", param("text"), MediaType.TEXT_PLAIN,
                StringHttpMessageConverter.class, request(), response());

        ApiResult<?> parsed = objectMapper.readValue(result, ApiResult.class);
        assertThat(parsed.code()).isEqualTo("200");
        assertThat(parsed.data()).isEqualTo("hello");
    }

    @Test
    void skipsIgnoredResponseEntityAndPlainController() throws Exception {
        assertThat(advice.supports(param("ignored"), MappingJackson2HttpMessageConverter.class)).isFalse();
        assertThat(advice.supports(param("entity"), MappingJackson2HttpMessageConverter.class)).isFalse();

        Method method = PlainController.class.getDeclaredMethod("pojo");
        assertThat(advice.supports(new MethodParameter(method, -1),
                MappingJackson2HttpMessageConverter.class)).isFalse();
    }

    @Test
    void skipsWhenDisabledOrNonJsonConverter() throws Exception {
        ApiResultResponseAdvice disabled = new ApiResultResponseAdvice(objectProvider(), new ResponseProperties(false));
        assertThat(disabled.supports(param("pojo"), MappingJackson2HttpMessageConverter.class)).isFalse();
        assertThat(advice.supports(param("pojo"), StringHttpMessageConverter.class)).isTrue();
    }

    @Test
    void wrapsHttpResponseThroughMockMvc() throws Exception {
        org.springframework.test.web.servlet.MockMvc mvc =
                org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup(new TestController())
                        .setControllerAdvice(advice)
                        .build();

        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/pojo"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.status().isOk())
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.code").value("200"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath("$.data").value("pojo"));

        mvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/ignored"))
                .andExpect(org.springframework.test.web.servlet.result.MockMvcResultMatchers.content().string("raw"));
    }

    @Test
    void passesThroughExistingApiResult() throws Exception {
        ApiResult<String> existing = ApiResult.success("already");

        Object result = advice.beforeBodyWrite(existing, param("pojo"), MediaType.APPLICATION_JSON,
                MappingJackson2HttpMessageConverter.class, request(), response());

        assertThat(result).isSameAs(existing);
    }

    private static ServletServerHttpRequest request() {
        return new ServletServerHttpRequest(new MockHttpServletRequest());
    }

    private static ServletServerHttpResponse response() {
        return new ServletServerHttpResponse(new MockHttpServletResponse());
    }

    private ObjectProvider<ObjectMapper> objectProvider() {
        DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
        factory.registerSingleton("objectMapper", objectMapper);
        return factory.getBeanProvider(ObjectMapper.class);
    }

    private MethodParameter param(String methodName) throws Exception {
        Method method = TestController.class.getDeclaredMethod(methodName);
        return new MethodParameter(method, -1);
    }

    @RestController
    static class TestController {
        @org.springframework.web.bind.annotation.GetMapping("/pojo")
        String pojo() {
            return "pojo";
        }

        String text() {
            return "text";
        }

        @IgnoreApiResult
        @org.springframework.web.bind.annotation.GetMapping("/ignored")
        String ignored() {
            return "raw";
        }

        ResponseEntity<String> entity() {
            return ResponseEntity.ok("entity");
        }
    }

    static class PlainController {
        String pojo() {
            return "pojo";
        }
    }
}
