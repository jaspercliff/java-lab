package com.jasper.creational.factory.abstractFactory;

// 产品族接口
interface Button {
    void click();
}

interface TextBox {
    void type();
}

// 具体产品 - Windows 风格
class WindowsButton implements Button {
    public void click() {
        System.out.println("点击 Windows 按钮");
    }
}

class WindowsTextBox implements TextBox {
    public void type() {
        System.out.println("输入 Windows 文本");
    }
}

// 具体产品 - Mac 风格
class MacButton implements Button {
    public void click() {
        System.out.println("点击 Mac 按钮");
    }
}

class MacTextBox implements TextBox {
    public void type() {
        System.out.println("输入 Mac 文本");
    }
}

// 抽象工厂接口
interface GUIFactory {
    Button createButton();
    TextBox createTextBox();
}

// 具体工厂
class WindowsFactory implements GUIFactory {
    public Button createButton() {
        return new WindowsButton();
    }
    public TextBox createTextBox() {
        return new WindowsTextBox();
    }
}

class MacFactory implements GUIFactory {
    public Button createButton() {
        return new MacButton();
    }
    public TextBox createTextBox() {
        return new MacTextBox();
    }
}

// 使用
public class Main {
    public static void main(String[] args) {
        GUIFactory factory = new MacFactory();  // 可切换为 WindowsFactory
        Button button = factory.createButton();
        TextBox textBox = factory.createTextBox();

        button.click();      // 输出：点击 Mac 按钮
        textBox.type();      // 输出：输入 Mac 文本
    }
}