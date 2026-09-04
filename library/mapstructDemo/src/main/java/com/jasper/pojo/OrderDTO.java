package com.jasper.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class OrderDTO {
    private int id;
    private Customer customer;
    private List<ProductDTO> product;
}
