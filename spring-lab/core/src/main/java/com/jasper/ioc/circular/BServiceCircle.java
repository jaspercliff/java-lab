package com.jasper.ioc.circular;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BServiceCircle {
    public final AServiceCircle aServiceCircle;

    @Autowired // 构造器注入
    public BServiceCircle(AServiceCircle aServiceCircle) {
        this.aServiceCircle = aServiceCircle;
    }
}
