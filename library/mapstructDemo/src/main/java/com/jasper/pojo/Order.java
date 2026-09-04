package com.jasper.pojo;

import lombok.Data;

import java.util.List;

@Data
public class Order {
    private Long id;
    private Customer customer;
    private List<Product> product;
}
