package com.jasper.web;

import com.jasper.security.core.SensitiveFieldMatcher;
import com.jasper.web.config.properties.AccessLogProperties;
import com.jasper.web.filter.AccessLogFilter;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.HandlerMapping;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AccessLogFilterTest {

    private static final AccessLogProperties DEFAULTS = new AccessLogProperties(true, 1000, "traceId", true, true, 2048);
    private final SensitiveFieldMatcher matcher =
            new SensitiveFieldMatcher(List.of("password", "token", "phone", "secret"));
    private final AccessLogFilter filter = new AccessLogFilter(DEFAULTS, matcher);

    @Test
    void masksSensitiveQueryParams() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/user");
        request.addParameter("name", "jasper");
        request.addParameter("password", "123456");
        request.addParameter("access_token", "abc");

        String suffix = filter.querySuffix(request);

        assertThat(suffix).startsWith("?")
                .contains("name=jasper")
                .contains("password=******")
                .contains("access_token=******")
                .doesNotContain("123456")
                .doesNotContain("abc");
    }

    @Test
    void keepsTailForPhoneParam() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/user");
        request.addParameter("userPhone", "13812345678");

        assertThat(filter.querySuffix(request))
                .contains("userPhone=138****5678")
                .doesNotContain("13812345678");
    }

    @Test
    void skipsQueryForMultipart() {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/upload");
        request.setContentType("multipart/form-data");
        request.addParameter("name", "jasper");

        assertThat(filter.querySuffix(request)).isEmpty();
    }

    @Test
    void masksPathVariableByItsName() {
        // Filter 进入时拿不到变量名，靠 MVC 写入的 attribute 判断：模板 + 变量名→值
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/user/13812345678/order/9");
        request.setAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE, "/api/user/{userPhone}/order/{id}");
        request.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE,
                Map.of("userPhone", "13812345678", "id", "9"));

        assertThat(filter.target(request))
                .isEqualTo("/api/user/138****5678/order/9")
                .doesNotContain("13812345678");
    }

    @Test
    void fallsBackToRawUriWhenNoHandlerAttributes() {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/user/123");
        assertThat(filter.target(request)).isEqualTo("/api/user/123");
    }

    @Test
    void writesBackCachedResponseBody() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/user");
        MockHttpServletResponse response = new MockHttpServletResponse();
        // 模拟下游读取请求体并写入响应体
        MockFilterChain chain = new MockFilterChain() {
            @Override
            public void doFilter(jakarta.servlet.ServletRequest req, jakarta.servlet.ServletResponse resp)
                    throws java.io.IOException {
                String body = new String(req.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                assertThat(body).contains("\"password\":\"123456\"");
                resp.getWriter().write("{\"phone\":\"13812345678\"}");
            }
        };
        request.setContent("{\"password\":\"123456\"}".getBytes(StandardCharsets.UTF_8));

        filter.doFilter(request, response, chain);

        // copyBodyToResponse 生效：响应体未被日志吞掉
        assertThat(response.getContentAsString()).isEqualTo("{\"phone\":\"13812345678\"}");
    }

    @Test
    void passesRequestThroughChain() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/user");
        MockFilterChain chain = new MockFilterChain();

        filter.doFilter(request, new MockHttpServletResponse(), chain);

        assertThat(chain.getRequest()).isNotNull();
    }

    @Test
    void masksSensitiveFieldsInJsonBody() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/user");
        request.setContentType("application/json");
        request.setContent(("{\"password\":\"123456\",\"phone\":\"13812345678\","
                + "\"nested\":{\"token\":\"abc\"},\"items\":[{\"secret\":\"xyz\"}]}")
                .getBytes(StandardCharsets.UTF_8));

        String body = readRequestBodyViaChain(request);

        assertThat(body).contains("\"password\":\"******\"")
                .contains("\"phone\":\"138****5678\"")
                .contains("\"token\":\"******\"")
                .contains("\"secret\":\"******\"")
                .doesNotContain("123456", "13812345678", "\"abc\"", "\"xyz\"");
    }

    @Test
    void disablesBodyCaptureWhenConfigured() throws Exception {
        AccessLogProperties noBody = new AccessLogProperties(true, 1000, "traceId", false, false, 2048);
        AccessLogFilter noBodyFilter = new AccessLogFilter(noBody, matcher);
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/user");
        request.setContent("{\"password\":\"123456\"}".getBytes(StandardCharsets.UTF_8));

        MockFilterChain chain = new MockFilterChain();
        noBodyFilter.doFilter(request, new MockHttpServletResponse(), chain);

        // 关闭捕获时透传原始 request，下游仍读得到 body
        assertThat(chain.getRequest()).isSameAs(request);
    }

    /** 借助 filter 的响应体回写链路，取出脱敏后的请求体断言（通过反射调用私有方法）。 */
    private String readRequestBodyViaChain(MockHttpServletRequest request) throws Exception {
        org.springframework.web.util.ContentCachingRequestWrapper cached =
                new org.springframework.web.util.ContentCachingRequestWrapper(request, 2048);
        // 触发下游读取以填充缓存
        cached.getInputStream().readAllBytes();
        java.lang.reflect.Method method = AccessLogFilter.class.getDeclaredMethod(
                "readRequestBody", org.springframework.web.util.ContentCachingRequestWrapper.class);
        method.setAccessible(true);
        return (String) method.invoke(filter, cached);
    }
}
