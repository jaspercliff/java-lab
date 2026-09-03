package com.jasper.vt;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Demo1 {
    public static void main(String[] args) throws InterruptedException {
        Thread vt = Thread.startVirtualThread(()->{
            try {
                System.out.println("start");
                Thread.sleep(2000);
                System.out.println("end");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
//        虚拟线程是守护线程（Daemon Thread），且主线程执行完代码后会立即退出，不会等待虚拟线程结束
        vt.join();
    }
}