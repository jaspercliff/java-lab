package com.jasper.extension;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class ThreadModeLifecycleExtension implements
        BeforeEachCallback,
        BeforeTestExecutionCallback,
        AfterTestExecutionCallback,
        AfterEachCallback {

    @Override
    public void beforeEach(ExtensionContext context) {
        log("1. BeforeEachCallback");
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) {
        log("3. BeforeTestExecutionCallback");
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        log("AfterTestExecutionCallback");
    }

    @Override
    public void afterEach(ExtensionContext context) {
        log("AfterEachCallback");
    }

    private void log(String stage) {
        System.out.printf(
                "[%s] thread=%s%n",
                stage,
                Thread.currentThread().getName()
        );
    }
}