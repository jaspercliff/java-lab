package com.jasper.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Person {
    private int id;
    private String name;
    private int age;
    public Person(int id, String name) {
        this.id = id;
        this.name = name;
    }

}
