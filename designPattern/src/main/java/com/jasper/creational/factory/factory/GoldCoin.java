package com.jasper.creational.factory.factory;

public class GoldCoin implements Coin {
    static final String DESCRIPTION = "this is gold coin";
    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}
