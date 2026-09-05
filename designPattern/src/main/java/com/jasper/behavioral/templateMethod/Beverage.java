package com.jasper.behavioral.templateMethod;

public abstract class Beverage {
    public final void BeverageTemplate() {
        boilWater();
        brew();
        pourInCup();
        if (customAddCondiments()){
            addCondiments();
        }
    }

//    公共操作
    private void boilWater() {
        System.out.println("Boiling water");
    }
    private void pourInCup() {
        System.out.println("Pouring into cup");
    }

    /**
     * Determines if custom condiments(调味品) should be added to the beverage(饮料).
     * Hook method ，子类可以选择性覆盖
     * @return true if custom condiments are to be added, false otherwise
     */
    protected boolean customAddCondiments() {
        return true;
    }

    /**
     * 基本操作，子类必须实现的方法
     */
    protected abstract void brew();
    protected abstract void addCondiments();
}
