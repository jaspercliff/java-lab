package com.jasper.pojo.entity;

public class Cup {
    private String name;
    private int high;

    public Cup(String name, int high) {
        this.name = name;
        this.high = high;
    }

    public int getHigh() {
        return high;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Cup{name='" + name + "', high=" + high + "}";
    }
}