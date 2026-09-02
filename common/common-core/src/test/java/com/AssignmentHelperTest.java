package com;

import lombok.Data;
import org.junit.jupiter.api.Test;

import static com.jasper.utils.AssignmentHelper.null2Default;
import static org.junit.jupiter.api.Assertions.*;

import java.util.function.Supplier;
import java.util.function.BiConsumer;

class AssignmentHelperTest {

    // 测试类用于验证null2Default方法
    @Data
    static class TestObject {
        private String value;
    }

    @Test
    void testNull2Default_WhenSupplierReturnsNull_ShouldSetDefaultValue() {
        // 准备测试数据
        TestObject target = new TestObject();
        Supplier<String> nullSupplier = () -> null; // 模拟返回null的Supplier
        BiConsumer<TestObject, String> setter = TestObject::setValue;
        String defaultValue = "default";

        // 执行方法
        null2Default(target, nullSupplier, setter, defaultValue);

        // 验证结果
        assertEquals(defaultValue, target.getValue());
    }

    @Test
    void testNull2Default_WhenSupplierReturnsValue_ShouldSetSuppliedValue() {
        // 准备测试数据
        TestObject target = new TestObject();
        target.setValue("test");
        Supplier<String> supplier = target::getValue;
        BiConsumer<TestObject, String> setter = TestObject::setValue;
        String defaultValue = "default";

        // 执行方法
        null2Default(target, supplier, setter, defaultValue);

        // 验证结果
        assertEquals("test", target.getValue());
    }


    @Test
    void testNull2Default_WithNullDefaultValue() {
        // 准备测试数据
        TestObject target = new TestObject();
        target.setValue(null);
        Supplier<String> supplier = target::getValue;
        BiConsumer<TestObject, String> setter = TestObject::setValue;
        String defaultValue = "default";

        // 执行方法
        null2Default(target, supplier, setter, defaultValue);

        // 验证结果
        assertEquals("default", target.getValue());
    }

}