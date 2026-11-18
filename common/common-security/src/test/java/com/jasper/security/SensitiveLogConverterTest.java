package com.jasper.security;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.LoggingEvent;
import com.jasper.security.log.SensitiveLogConverter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** 日志脱敏 Converter 测试：覆盖键值对、裸值、自定义字段与边界。 */
class SensitiveLogConverterTest {

    private final LoggerContext context = new LoggerContext();
    private final Logger logger = context.getLogger(SensitiveLogConverterTest.class);

    private final SensitiveLogConverter converter = new SensitiveLogConverter();

    @Test
    void masksKeyValuePairsByFieldName() {
        assertThat(convert("login phone=13812345678 password=123456 token:abc123"))
                .isEqualTo("login phone=138****5678 password=****** token:******");
    }

    @Test
    void masksQuotedKeyValuePairs() {
        assertThat(convert("req {\"phone\":\"13812345678\",\"password\":\"123456\"}"))
                .isEqualTo("req {\"phone\":\"138****5678\",\"password\":\"******\"}");
    }

    @Test
    void masksBarePhoneAndIdCard() {
        assertThat(convert("user 13812345678 id 110101199001011234"))
                .isEqualTo("user 138****5678 id 1101**********1234");
    }

    @Test
    void keepsNonSensitiveFieldsAndValues() {
        assertThat(convert("user=jasper status=200 cost=15ms"))
                .isEqualTo("user=jasper status=200 cost=15ms");
    }

    @Test
    void doesNotOvermaskAdjacentKeys() {
        // url 值中嵌套 token=abc，脱敏不能吞掉原始 token 键
        assertThat(convert("url=https://host?token=abc123"))
                .isEqualTo("url=https://host?token=******");
    }

    @Test
    void handlesFormattedMessageWithArguments() {
        LoggingEvent event = new LoggingEvent(
                SensitiveLogConverterTest.class.getName(), logger, Level.INFO,
                "phone={} name={}", null, new Object[]{"13812345678", "jasper"});
        assertThat(converter.convert(event)).isEqualTo("phone=138****5678 name=jasper");
    }

    @Test
    void optionOverridesDefaultFields() {
        SensitiveLogConverter custom = new SensitiveLogConverter();
        custom.setOptionList(java.util.List.of("innerToken"));
        custom.start();
        assertThat(custom.convert(event("innerToken=abc phone=13812345678")))
                .isEqualTo("innerToken=****** phone=138****5678");
    }

    @Test
    void handlesNullAndEmptyMessage() {
        assertThat(converter.convert(event(null))).isNull();
        assertThat(converter.convert(event(""))).isEmpty();
    }

    @Test
    void notMasksShortOrMalformedValues() {
        // 非 11 位手机号、非 18 位身份证不被值级规则误伤
        assertThat(convert("num=12345 text=11010119900101"))
                .isEqualTo("num=12345 text=11010119900101");
    }

    private LoggingEvent event(String message) {
        return new LoggingEvent(SensitiveLogConverterTest.class.getName(), logger, Level.INFO, message, null, null);
    }

    private String convert(String message) {
        return converter.convert(event(message));
    }
}
