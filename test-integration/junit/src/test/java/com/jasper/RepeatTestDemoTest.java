package com.jasper;

import org.junit.jupiter.api.RepeatedTest;

/**
 * repeat and parameterized all based on testTemplate
 */
public class RepeatTestDemoTest {

    @RepeatedTest(3)
    void test(){
        System.out.println("3.test");
    }
}
