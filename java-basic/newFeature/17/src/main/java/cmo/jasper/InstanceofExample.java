package cmo.jasper;

public class InstanceofExample {

    public static void processObject(Object obj) {
//        if (obj instanceof String) {
//            String s = (String) obj;
//            System.out.println(s.length());
//        }
        // 1. 基础用法：自动转型
        if (obj instanceof String s) {
            System.out.println("字符串大写: " + s.toUpperCase());
        } 
    }

    public static void main(String[] args) {
        processObject("Hello");
        processObject(10);
    }
}