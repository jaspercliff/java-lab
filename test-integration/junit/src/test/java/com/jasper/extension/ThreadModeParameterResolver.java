package com.jasper.extension;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

public class ThreadModeParameterResolver
        implements ParameterResolver {

    private final ThreadMode mode;

    public ThreadModeParameterResolver(ThreadMode mode) {
        this.mode = mode;
    }

    // 参数是否由自己处理
    @Override
    public boolean supportsParameter(
            ParameterContext parameterContext,
            ExtensionContext extensionContext) {

        return parameterContext.getParameter()
                .getType() == ThreadMode.class; //只要是ThreadMode 类型就可以处理
    }
    //                                    负责返回参数值
    @Override
    public Object resolveParameter(
            ParameterContext parameterContext,
            ExtensionContext extensionContext) {

        return mode;
    }

}