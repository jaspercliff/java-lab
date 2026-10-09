package com.jasper.pojo.dto;

import java.math.BigDecimal;

/**
 * for producer-general
 */
public record Product(
        Long id,
        String name,
        BigDecimal price,
        Integer stock
) {
}