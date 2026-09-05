package com.jasper.creational.factory.factory;

public class CopperCoin implements Coin{
    static final String DESCRIPTION = "this is Copper Coin";
    @Override
    public String getDescription() {
        return DESCRIPTION;
    }
}
