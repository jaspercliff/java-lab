package com.jasper.ioc.lifecycle.aware.pay;

import java.math.BigDecimal;

public interface PayStrategy {
    void pay(BigDecimal amount);
    String getType(); // 返回该策略的唯一标识
}