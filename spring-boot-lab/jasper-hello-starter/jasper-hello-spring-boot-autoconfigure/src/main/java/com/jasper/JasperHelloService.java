package com.jasper;

import com.jasper.config.JasperHelloProperties;

public class JasperHelloService {

    private final JasperHelloProperties properties;

    public JasperHelloService(JasperHelloProperties properties) {
        this.properties = properties;
    }

    public String hello() {
        return "Hello, " + properties.getName();
    }
}