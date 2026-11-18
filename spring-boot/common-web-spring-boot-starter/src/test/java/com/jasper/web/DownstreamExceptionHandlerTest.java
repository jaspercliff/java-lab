package com.jasper.web;

import com.jasper.result.ApiResult;
import com.jasper.web.exception.downStream.DownstreamError;
import com.jasper.web.exception.downStream.DownstreamExceptionHandler;
import com.jasper.web.exception.downStream.FeignExceptionHandler;
import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(ChineseLocaleExtension.class)
class DownstreamExceptionHandlerTest {

    private final HttpServletRequest request = mock(HttpServletRequest.class);
    private final DownstreamExceptionHandler handler = new DownstreamExceptionHandler(TestMessages.source());

    @Test
    void responseExceptionBecomes502WithoutLeakingDownstreamCode() {
        RestClientResponseException ex = new HttpClientErrorException(HttpStatus.NOT_FOUND);

        ApiResult<DownstreamError> result = handler.handleResponseException(ex, request);

        assertThat(result.code()).isEqualTo("502");
        assertThat(result.message()).isEqualTo("下游服务调用失败");
        assertThat(result.data().downstreamStatus()).isEqualTo(404);
        assertThat(result.message()).doesNotContain("Not Found");
    }

    @Test
    void clientExceptionBecomes503() {
        ApiResult<Void> result = handler.handleClientException(new RestClientException("timeout"), request);

        assertThat(result.code()).isEqualTo("503");
        assertThat(result.message()).isEqualTo("服务不可用");
    }

    @Test
    void feignExceptionUsesSameCode() {
        FeignException ex = mock(FeignException.class);
        when(ex.status()).thenReturn(500);
        when(ex.getMessage()).thenReturn("feign error");

        ApiResult<DownstreamError> result = new FeignExceptionHandler(TestMessages.source()).handleFeign(ex, request);

        assertThat(result.code()).isEqualTo("502");
        assertThat(result.data().downstreamStatus()).isEqualTo(500);
    }
}
