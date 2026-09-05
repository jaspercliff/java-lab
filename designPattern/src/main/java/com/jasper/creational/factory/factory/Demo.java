package com.jasper.creational.factory.factory;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Demo {
    public static void main(String[] args) {
        Coin instance = CoinFactory.getInstance(CoinType.Gold);
        Coin  instance1= CoinFactory.getInstance(CoinType.Copper);
        log.info(instance.getDescription());
        log.info(instance1.getDescription());
    }
}
