package jasper.basic.thread.interrupt;

import static java.lang.Thread.interrupted;

public class Demo1 {
    public static void main(String[] args) {
        // 如果一个线程的run方法是一个无限循环 并且没有sleep等会抛出interruptException的方法
        // 则调用interrupt方法不会中断线程，但是调用interrupt会设置线程的中断标志，此时
        // interrupted（）方法会返回true
        Thread thread = new Thread(() -> {
            while (!interrupted()) {
                System.out.println(Thread.currentThread().getState()+"--" + Thread.currentThread().getName());
            }
            System.out.println(Thread.currentThread().getName() + "end");
        });
        thread.start();
        for (int i = 0; i < 4; i++) {
            System.out.println("main");
        }
        System.out.println(Thread.currentThread().getName()+" end");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        thread.interrupt();
    }
}