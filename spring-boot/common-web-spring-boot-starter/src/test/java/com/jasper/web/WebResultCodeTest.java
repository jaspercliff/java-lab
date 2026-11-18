package com.jasper.web;

import com.jasper.api.IEnum;
import com.jasper.web.constants.WebResultCode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WebResultCodeTest {

    @Test
    void implementsBothResultCodeAndEnumContracts() {
        assertThat(IEnum.parseOf(WebResultCode.class, 413)).isEqualTo(WebResultCode.PAYLOAD_TOO_LARGE);
        assertThat(IEnum.parseOf(WebResultCode.class, "502")).isEqualTo(WebResultCode.DOWNSTREAM_ERROR);
        assertThat(WebResultCode.PAYLOAD_TOO_LARGE.getCode()).isEqualTo(413);
        assertThat(WebResultCode.PAYLOAD_TOO_LARGE.getMsg()).isEqualTo("上传文件过大");
        assertThat(WebResultCode.PAYLOAD_TOO_LARGE.getDesc()).isEqualTo(WebResultCode.PAYLOAD_TOO_LARGE.getMsg());
    }
}
