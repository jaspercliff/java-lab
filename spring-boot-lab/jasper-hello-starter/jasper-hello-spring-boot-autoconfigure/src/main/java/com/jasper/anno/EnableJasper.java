package com.jasper.anno;

import com.jasper.config.JasperImportSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(JasperImportSelector.class)
public @interface EnableJasper {
}