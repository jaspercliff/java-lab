package com.jasper.ioc.lifecycle.aware.pay;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component // 名字默认是 alipayStrategy
public class AlipayStrategy implements PayStrategy {
    @Override
    public String getType() { return "ALIPAY"; }
    
    @Override
    public void pay(BigDecimal amount) { /* ... */ }
}