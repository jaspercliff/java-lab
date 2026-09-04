package com.jasper.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductDTO {
    private long id;
    private String name;
    private String des;
}
