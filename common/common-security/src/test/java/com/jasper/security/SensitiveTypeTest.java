package com.jasper.security;

import com.jasper.api.IEnum;
import com.jasper.security.core.SensitiveType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SensitiveTypeTest {

    @Test
    void implementsIEnumAndParsesByCode() {
        assertThat(SensitiveType.PHONE.getCode()).isEqualTo("phone");
        assertThat(SensitiveType.PHONE.getDesc()).contains("手机号");
        // IEnum 的静态方法只能通过接口调用
        assertThat(IEnum.parseOf(SensitiveType.class, "PHONE")).isEqualTo(SensitiveType.PHONE);
        assertThat(IEnum.parseOf(SensitiveType.class, " id_card ")).isEqualTo(SensitiveType.ID_CARD);
    }

    @Test
    void masksByStrategy() {
        assertThat(SensitiveType.MASK.apply("123456")).isEqualTo("******");
        assertThat(SensitiveType.KEEP_EDGE.apply("abcdef")).isEqualTo("a******f");
        assertThat(SensitiveType.PHONE.apply("13812345678")).isEqualTo("138****5678");
        assertThat(SensitiveType.ID_CARD.apply("110101199001011234")).isEqualTo("1101**********1234");
    }
}
