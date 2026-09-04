package jasper.basic.thread.lifecycle;

import static java.lang.Thread.interrupted;

public class InterruptedDemo1 {
    public static void main(String[] args) {
        Thread thread = new Thread(() -> {
//                  检查当前线程是否被中断  调用完之后会清楚线程的中断状态
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
            Thread.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        thread.interrupt();
    }
}