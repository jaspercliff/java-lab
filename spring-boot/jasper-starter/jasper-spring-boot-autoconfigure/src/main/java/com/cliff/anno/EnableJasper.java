package com.cliff.anno;

import com.cliff.config.JasperImportSelector;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(JasperImportSelector.class)
public @interface EnableJasper {
}