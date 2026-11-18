package com.jasper.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jasper.web.config.JacksonCustomizer;
import com.jasper.web.config.properties.JacksonProperties;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class JacksonCustomizerTest {

    @Test
    void appliesDatePatternLongToStringAndNonNull() throws Exception {
        JacksonProperties properties =
                new JacksonProperties(true, "yyyy-MM-dd HH:mm:ss", "Asia/Shanghai", true);
        Jackson2ObjectMapperBuilder builder = Jackson2ObjectMapperBuilder.json();
        new JacksonCustomizer(properties).customize(builder);
        ObjectMapper mapper = builder.build();

        String json = mapper.writeValueAsString(new Payload(
                1234567890123L,
                LocalDateTime.of(2026, 11, 17, 16, 40, 35),
                LocalDate.of(2026, 11, 17),
                null));

        assertThat(json).contains("\"id\":\"1234567890123\"")
                .contains("\"time\":\"2026-11-17 16:40:35\"")
                .contains("\"date\":\"2026-11-17\"")
                .doesNotContain("missing");
    }

    @Test
    void disabledCustomizerLeavesJacksonUntouched() throws Exception {
        JacksonProperties properties =
                new JacksonProperties(false, "yyyy-MM-dd HH:mm:ss", "Asia/Shanghai", true);
        Jackson2ObjectMapperBuilder builder = Jackson2ObjectMapperBuilder.json();
        new JacksonCustomizer(properties).customize(builder);
        ObjectMapper mapper = builder.build();

        assertThat(mapper.writeValueAsString(1234567890123L)).isEqualTo("1234567890123");
    }

    private record Payload(long id, LocalDateTime time, LocalDate date, String missing) {
    }
}
