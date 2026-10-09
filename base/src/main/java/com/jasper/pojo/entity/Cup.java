package com.jasper.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Cup {
    private String name;
    private int high;

    @Override
    public String toString() {
        return "Cup{name='" + name + "', high=" + high + "}";
    }
}