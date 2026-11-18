package com.jasper.security;

import com.jasper.security.annotation.Sensitive;
import com.jasper.security.core.SensitiveType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** 验证 @Sensitive 在 Jackson 2 与 Jackson 3 序列化时都真正生效。 */
class SensitiveAnnotationTest {

    private final com.fasterxml.jackson.databind.ObjectMapper mapper2 =
            new com.fasterxml.jackson.databind.ObjectMapper();
    private final tools.jackson.databind.ObjectMapper mapper3 =
            tools.jackson.databind.json.JsonMapper.builder().build();

    @Test
    void masksAnnotatedFieldsWithJackson2() throws Exception {
        String json = mapper2.writeValueAsString(dto());
        assertMasked(json);
    }

    @Test
    void masksAnnotatedFieldsWithJackson3() {
        String json = mapper3.writeValueAsString(dto());
        assertMasked(json);
    }

    private static void assertMasked(String json) {
        assertThat(json).contains("\"password\":\"******\"")
                .contains("\"phone\":\"138****5678\"")
                .contains("\"idCard\":\"1101**********1234\"")
                .contains("\"name\":\"jasper\"")
                .doesNotContain("123456", "13812345678", "110101199001011234");
    }

    private static Dto dto() {
        return new Dto("123456", "13812345678", "110101199001011234", "jasper");
    }

    static class Dto {
        @Sensitive
        public String password;
        @Sensitive(SensitiveType.PHONE)
        public String phone;
        @Sensitive(SensitiveType.ID_CARD)
        public String idCard;
        public String name;

        Dto(String password, String phone, String idCard, String name) {
            this.password = password;
            this.phone = phone;
            this.idCard = idCard;
            this.name = name;
        }
    }
}
