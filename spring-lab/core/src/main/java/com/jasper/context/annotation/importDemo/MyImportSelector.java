package com.jasper.context.annotation.importDemo;

import com.jasper.service.ServiceA;
import com.jasper.service.ServiceB;

import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @author jasper
 * @since 2026-09-07 <br>
 *     Spring 会调用其 `selectImports()` 方法，该方法返回一个字符串数组（类的全限定名），Spring 会将这些类全部导入到容器中
 */
public class MyImportSelector implements ImportSelector {
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        return new String[] {ServiceA.class.getName(), ServiceB.class.getName()};
    }
}
