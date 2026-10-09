package com.cliff.config;

import org.jspecify.annotations.NonNull;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

public class JasperImportSelector implements ImportSelector {

    @Override
    public String[] selectImports(@NonNull AnnotationMetadata metadata) {
        return new String[]{
                JasperConfiguration.class.getName(),
        };
    }
}