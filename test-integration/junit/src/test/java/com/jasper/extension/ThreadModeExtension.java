package com.jasper.extension;

import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContext;
import org.junit.jupiter.api.extension.TestTemplateInvocationContextProvider;

import java.util.List;
import java.util.stream.Stream;

/**
 * 决定生成哪些测试调用
 */
public class ThreadModeExtension
        implements TestTemplateInvocationContextProvider {

    private static final List<ThreadMode> MODES = List.of(
            ThreadMode.SAME_THREAD,
            ThreadMode.CONCURRENT,
            ThreadMode.CUSTOM
    );

    /**
     * 声明当前 Provider 是否适用于该测试方法
     * 当前测试模板应该由哪些 Provider 提供执行上下文
     */
    @Override
    public boolean supportsTestTemplate(ExtensionContext context) {
        return true;
    }

    /**
     * 描述每一次测试调用
     */
    @Override
    public Stream<TestTemplateInvocationContext>
    provideTestTemplateInvocationContexts(ExtensionContext context) {
        //提供一个或多个 TestTemplateInvocationContext
        return MODES.stream().map(mode ->
                new TestTemplateInvocationContext() {

                    @Override
                    public String getDisplayName(int invocationIndex) {
                        return "========[" + invocationIndex + "] " + mode.name();
                    }

                    /**
                     * getAdditionalExtensions() 不仅可以提供 ParameterResolver，还可以根据每次调用的需要注册其他扩展
                     * ，例如 BeforeTestExecutionCallback、AfterTestExecutionCallback 或 ExecutionCondition 等
                     * ParameterResolver：向测试方法注入参数。
                     * BeforeEachCallback：在每次测试方法执行前回调。
                     * AfterEachCallback：在每次测试方法执行后回调。
                     * BeforeTestExecutionCallback：在测试方法即将执行时回调。
                     * AfterTestExecutionCallback：在测试方法执行后回调
                     * BeforeEachCallback > beforeEach > BeforeTestExecutionCallback
                     */
                    @Override
                    public List<Extension> getAdditionalExtensions() {
                        return List.of(
                                new ThreadModeLifecycleExtension(),
                                new ThreadModeParameterResolver(mode)
                        );
                    }
                }
        );
    }
}