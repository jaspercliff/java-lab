package com.jasper.security;

import com.jasper.security.core.SensitiveFieldMatcher;
import com.jasper.security.core.SensitiveType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SensitiveFieldMatcherTest {

    private final SensitiveFieldMatcher matcher =
            new SensitiveFieldMatcher(List.of("password", "token", "phone", "idCard"));

    @Test
    void matchesFieldNameIgnoringCaseAndPath() {
        assertThat(matcher.isSensitive("password")).isTrue();
        assertThat(matcher.isSensitive("user.PASSWORD")).isTrue();
        assertThat(matcher.isSensitive("profile.accessToken")).isTrue();
        assertThat(matcher.isSensitive("username")).isFalse();
        assertThat(matcher.isSensitive(null)).isFalse();
        assertThat(matcher.isSensitive(" ")).isFalse();
    }

    @Test
    void masksPasswordCompletelyAndKeepsNull() {
        assertThat(matcher.mask("password", "123456")).isEqualTo("******");
        assertThat(matcher.mask("password", null)).isNull();
        assertThat(matcher.mask("name", "jasper")).isEqualTo("jasper");
    }

    @Test
    void keepsTailForPhoneAndIdCard() {
        assertThat(matcher.mask("userPhone", "13812345678")).isEqualTo("138****5678");
        assertThat(matcher.mask("userIdCard", "110101199001011234")).isEqualTo("1101**********1234");
    }

    @Test
    void sensitiveTypeHandlesEdgeCases() {
        assertThat(SensitiveType.mask(null, SensitiveType.MASK)).isNull();
        assertThat(SensitiveType.mask("", SensitiveType.MASK)).isEmpty();
        assertThat(SensitiveType.mask("ab", SensitiveType.KEEP_EDGE)).isEqualTo("******");
        assertThat(SensitiveType.mask("abc123", SensitiveType.KEEP_EDGE)).isEqualTo("a******3");
        assertThat(SensitiveType.mask("138123", SensitiveType.PHONE)).isEqualTo("1******3");
        // 14 位不是标准身份证号，退化为保留首尾
        assertThat(SensitiveType.mask("11010119900101", SensitiveType.ID_CARD)).isEqualTo("1******1");
    }
}
