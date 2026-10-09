package com.jasper.pojo.dto;

import java.math.BigDecimal;

/**
 * for producer-general
 */
public record CreateProductRequest(
        String name,
        BigDecimal price,
        Integer stock
) {
}