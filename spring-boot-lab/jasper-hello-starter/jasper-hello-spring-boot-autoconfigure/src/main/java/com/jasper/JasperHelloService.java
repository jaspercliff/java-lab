package com.jasper;

public class JasperHelloService {

    private final JasperHelloProperties properties;

    public JasperHelloService(JasperHelloProperties properties) {
        this.properties = properties;
    }

    public String hello() {
        return "Hello, " + properties.getName();
    }
}