package com.jasper.optional;

import com.jasper.pojo.entity.Person;

import java.util.Optional;

public class Demo1 {
    public static void main(String[] args) {
        Optional<Person> optional = Optional.ofNullable(getUserById(1));
        optional.ifPresent(System.out::println);
    }

    private static Person getUserById(Integer id) {
        return null;
    }
}
