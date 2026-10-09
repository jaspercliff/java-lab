package com.jasper.controller;

import com.jasper.pojo.dto.CreateProductRequest;
import com.jasper.pojo.dto.Product;
import com.jasper.result.ApiResponse;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/products")
public class ProductController {

    /**
     * GET /producer/products/1001
     * just test return object
     */
    @GetMapping("/{id}")
    public Product getProduct(@PathVariable Long id) {

        return new Product(
                id,
                "机械键盘",
                new BigDecimal("299.00"),
                100
        );
    }

    /**
     * POST /producer/products
     */
    @PostMapping
    public ApiResponse<Product> createProduct(
            @RequestBody CreateProductRequest request) {

        Product product = new Product(
                1003L,
                request.name(),
                request.price(),
                request.stock()
        );

        return ApiResponse.success(product);
    }

    /**
     * POST /producer/products/1001/stock?quantity=50
     */
    @PostMapping("/{id}/stock")
    public ApiResponse<Product> updateStock(
            @PathVariable Long id,
            @RequestParam Integer quantity) {

        Product product = new Product(
                id,
                "机械键盘",
                new BigDecimal("299.00"),
                quantity
        );

        return ApiResponse.success(product);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Product> deleteProduct(@PathVariable Long id) {

        Product product = new Product(
                id,
                "机械键盘",
                new BigDecimal("299.00"),
                100
        );
        return ApiResponse.success(product);
    }
}