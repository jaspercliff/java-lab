package jasper.juc.volatileDemo;

public class VisibilityProblem {
//    private static  boolean flag = true;
    private static  volatile boolean flag = true;

    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            while (flag) {
//                System.out.println("running");//println 内部使用了 synchronized，引入了内存屏障，破坏了可见性问题的复现
            }
            System.out.println("Thread exits.");
        }).start();

        Thread.sleep(1000); // 确保线程启动并运行
        System.out.println("Set flag to false.");
        flag = false; // 修改flag，期望上面的线程能够看到这个修改并退出循环
    }
}