package com.thread.extension;

import com.jasper.constants.ThreadType;
import org.jspecify.annotations.NonNull;
import org.junit.jupiter.api.extension.*;

public class ThreadModeLifecycleExtension implements
        BeforeEachCallback ,
//        BeforeTestExecutionCallback,
//        AfterTestExecutionCallback,
        AfterEachCallback
{
    private final ThreadType threadType;

    private static final String SYS_PROP_KEY = "jasper.thread.type";
    private static final String STORE_KEY = "originalThreadTypeProp";

    public ThreadModeLifecycleExtension(ThreadType threadType) {
        this.threadType = threadType;
    }

    /**
     * 保存原始值到 Store  设置系统属性为 VIRTUAL
     */
    @Override
    public void beforeEach(@NonNull ExtensionContext context) throws Exception {
        String original = System.getProperty(SYS_PROP_KEY);
        // Store 可以理解为 JUnit 为扩展提供的一个上下文存储区，类似一个键值仓库
//        属于这个扩展的命名空间
//        不直接用局部变量 original？因为局部变量在 beforeEach() 方法返回后
//        就无法直接被 afterEach() 使用了，而 Store 可以让两个回调通过相同的命名空间和 key 共享这份数据
        context.getStore(ExtensionContext.Namespace.create(ThreadModeExtension.class))
                .put(STORE_KEY, original != null ? original : "");

        // Set property for this thread mode
        if (threadType == ThreadType.VIRTUAL) {
            System.setProperty(SYS_PROP_KEY, threadType.toString());
        } else {
//    没有设置就清楚 使用platform 默认的
            System.clearProperty(SYS_PROP_KEY);
        }
    }

    /**
     * 恢复现场，清理测试影响
     */
    @Override
    public void afterEach(@NonNull ExtensionContext context) throws Exception {
        // Restore original property value
        String original = context.getStore(ExtensionContext.Namespace.create(ThreadModeExtension.class))
                .get(STORE_KEY, String.class);
        if (original != null && !original.isEmpty()) {
            System.setProperty(SYS_PROP_KEY, original);
        } else {
            System.clearProperty(SYS_PROP_KEY);
        }
    }


}
