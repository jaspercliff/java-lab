package com.jasper.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jasper.security.annotation.Sensitive;
import com.jasper.security.core.SensitiveType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/** 验证 @Sensitive 在 Jackson 2 序列化时真正生效。 */
class SensitiveAnnotationTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void masksAnnotatedFieldsWithDifferentStrategies() throws Exception {
        String json = mapper.writeValueAsString(new Dto("123456", "13812345678", "110101199001011234", "jasper"));

        assertThat(json).contains("\"password\":\"******\"")
                .contains("\"phone\":\"138****5678\"")
                .contains("\"idCard\":\"1101**********1234\"")
                .contains("\"name\":\"jasper\"")
                .doesNotContain("123456", "13812345678", "110101199001011234");
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
