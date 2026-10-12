package com.jasper;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TestDemo {

    @Test
    public void test() {
        assertThat(10).isEqualTo(10);
        assertThat("hello world")
                .isNotNull()
                .startsWith("hello")
                .contains("world")
                .endsWith("world");
        int age = 18;
        assertThat(age)
                .as("用户年龄应该是 18 岁")
                .isEqualTo(18);
    }
}
