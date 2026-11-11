
package com.jasper;

import com.jasper.trace.config.TraceAutoConfiguration;
import com.jasper.trace.config.TraceProperties;
import com.jasper.trace.context.TraceIdProvider;
import com.jasper.trace.filter.TraceIdFilter;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class TraceIdFilterTest {

    private static final Pattern TRACE_ID_PATTERN =
            Pattern.compile("^[0-9a-f]{32}$");

    private TraceProperties properties;
    private TraceIdProvider provider;
    private TraceIdFilter filter;

    @BeforeEach
    void setUp() {
        properties = new TraceProperties(
                true,
                "X-Trace-Id",
                true,
                false,
                "traceId",
                new TraceProperties.Async(
                        true,
                        4,
                        16,
                        500,
                        60,
                        "trace-async-",
                        true,
                        30
                )
        );

        provider = new TraceIdProvider("traceId");
        filter = new TraceIdFilter(properties, provider);
    }

    @AfterEach
    void cleanup() {
        MDC.clear();
    }

    @Test
    void shouldGenerateTraceIdAndReturnInHeader() throws Exception {

        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();

        FilterChain chain = (req, res) -> {
            String traceId = MDC.get("traceId");

            assertNotNull(traceId);
            assertTrue(TRACE_ID_PATTERN.matcher(traceId).matches());
        };

        filter.doFilter(request, response, chain);

        String traceId = response.getHeader("X-Trace-Id");

        assertNotNull(traceId);
        assertTrue(TRACE_ID_PATTERN.matcher(traceId).matches());

        assertNull(MDC.get("traceId"));
    }

    @Test
    void shouldRestorePreviousMdc() throws Exception {
// 模拟当前线程原本就有 MDC
        MDC.put("traceId", "previous");

        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
// Filter 执行期间，应该使用新的 TraceId
        filter.doFilter(request, response, (req, res) -> {
            assertNotEquals("previous", MDC.get("traceId"));
        });
// Filter 执行结束后，应该恢复之前的 MDC
        assertEquals("previous", MDC.get("traceId"));
    }

    /**
     * 同一个 HttpServletRequest 被 Filter 多次处理时，是否复用第一次生成的 TraceId，而不是重新生成
     */
    @Test
    void shouldReuseRequestTraceId() throws Exception {

        var request = new MockHttpServletRequest();
        var firstResponse = new MockHttpServletResponse();

        filter.doFilter(request, firstResponse, (req, res) -> {
        });

        String first = firstResponse.getHeader("X-Trace-Id");

        assertNotNull(first);

        var secondResponse = new MockHttpServletResponse();

        filter.doFilter(request, secondResponse, (req, res) -> {
            assertEquals(first, MDC.get("traceId"));
        });

        assertEquals(
                first,
                secondResponse.getHeader("X-Trace-Id")
        );
    }

    /**
     * 外部传入的 TraceId 是否被接受，由配置控制。
     */
    @Test
    void shouldAcceptIncomingTraceIdWhenEnabled() throws Exception {

        properties = new TraceProperties(
                true,
                "X-Trace-Id",
                true,
                true,
                "traceId",
                properties.async()
        );

        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        String incomingTraceId = "a1b2c3d4e5f678901234567890abcdef";

        request.addHeader("X-Trace-Id", incomingTraceId);
        filter = new TraceIdFilter(properties, provider);

        filter.doFilter(request, response, (req, res) -> {
            assertEquals(incomingTraceId, MDC.get("traceId"));
        });

        assertEquals(
                incomingTraceId,
                response.getHeader("X-Trace-Id")
        );
    }

    @Test
    void shouldNotReturnResponseHeaderWhenDisabled() throws Exception {

        properties = new TraceProperties(
                true,
                "X-Trace-Id",
                false,
                false,
                "traceId",
                properties.async()
        );

        filter = new TraceIdFilter(properties, provider);

        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();

        filter.doFilter(request, response, (req, res) -> {
            assertNotNull(MDC.get("traceId"));
        });

        assertNull(response.getHeader("X-Trace-Id"));
    }


    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner()
                    .withConfiguration(
                            AutoConfigurations.of(
                                    TraceAutoConfiguration.class
                            )
                    );

    @Test
    void shouldRegisterFilterWhenEnabled() {
        contextRunner
                .withPropertyValues(
                        "jasper.trace.enabled=true"
                )
                .run(context -> {
                    assertThat(context)
                            .hasSingleBean(TraceIdFilter.class);
                });
    }

    @Test
    void shouldNotRegisterFilterWhenDisabled() {
        contextRunner
                .withPropertyValues(
                        "jasper.trace.enabled=false"
                )
                .run(context -> {
                    assertThat(context)
                            .doesNotHaveBean(TraceIdFilter.class);
                });
    }
}
