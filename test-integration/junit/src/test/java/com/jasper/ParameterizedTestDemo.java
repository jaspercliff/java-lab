package com.jasper;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class ParameterizedTestDemo {

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void test(int value) {
        System.out.println(value);
    }
}
