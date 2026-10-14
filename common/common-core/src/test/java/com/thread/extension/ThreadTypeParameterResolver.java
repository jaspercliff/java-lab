package com.thread.extension;

import com.jasper.constants.ThreadType;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

public class ThreadTypeParameterResolver implements ParameterResolver {

        private final ThreadType threadType;

        ThreadTypeParameterResolver(ThreadType threadType) {
            this.threadType = threadType;
        }

        @Override
        public boolean supportsParameter(ParameterContext parameterContext, @NonNull ExtensionContext extensionContext) {
            return parameterContext.getParameter().getType() == ThreadType.class;
        }

        @Override
        public Object resolveParameter(@NonNull ParameterContext parameterContext, @NonNull ExtensionContext extensionContext) {
            return threadType;
        }
    }