package com.jasper.creational.factory.factory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.function.Supplier;

@Getter
@RequiredArgsConstructor
public enum CoinType {
    Gold(GoldCoin::new),
    Copper(CopperCoin::new);
    private final Supplier<Coin> constructor;
}
