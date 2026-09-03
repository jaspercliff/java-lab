package com.jasper.functionInterface;

/**
 * @Author jasper @Date 2025-02-08
 *
 * @version 1.0 @Description
 */
public class Index {
    public static void main(String[] args){
        GreetingService greetingService = message -> System.out.println("hello"+ message);
        greetingService.sayMessage("world");
    }
}
