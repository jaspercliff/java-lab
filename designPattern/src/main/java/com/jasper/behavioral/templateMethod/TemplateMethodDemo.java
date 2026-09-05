package com.jasper.behavioral.templateMethod;

public class TemplateMethodDemo {
    public static void main(String[] args) {
        System.out.println("制作coffee=========");
        final Coffee coffee = new Coffee();
        coffee.BeverageTemplate();
        System.out.println("制作tea=========");
        final Tea tea = new Tea(false);
        tea.BeverageTemplate();
        System.out.println("制作tea========");
        final Tea tea2 = new Tea(true);
        tea2.BeverageTemplate();
    }
}
