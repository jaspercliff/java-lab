package com.jasper.extension;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

class ThreadModeTest {

    @BeforeEach
    void setUp() {
        System.out.println("2.Before each test");
    }
    /**
     * TestTemplate：声明这是一个测试模板，而不是普通测试方法。
     * TestTemplate 本身不负责生成测试用例，它负责声明一个测试模板；真正决定执行几次、每次如何执行的是 TestTemplateInvocationContextProvider 扩展
     * ExtendWith(ThreadModeExtension.class)：注册扩展，由扩展提供测试调用上下文
     */
    @TestTemplate
//    如果没有任何 TestTemplateInvocationContextProvider 支持这个方法，
//    JUnit 不会把它当成一个普通的 @Test 来执行，而是会报告模板缺少有效的 Provider
//You must register at least one TestTemplateInvocationContextProvider that supports @TestTemplate method
    @ExtendWith(ThreadModeExtension.class)
    void testThreadMode(ThreadMode mode) {
        System.out.println("当前测试模式：" + mode);
    }
}