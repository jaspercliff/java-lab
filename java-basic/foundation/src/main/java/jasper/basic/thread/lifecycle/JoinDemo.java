package jasper.basic.thread.lifecycle;
public class JoinDemo {
    public static void main(String[] args) throws InterruptedException {
        Thread thread = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                System.out.print(i + "A ");
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        Thread thread1 = new Thread(() -> {
            try {
//                在线程中掉起另外一个线程的join方法，会将当前线程挂起
                thread.join();
            } catch (InterruptedException e) {
                System.out.println("线程被中断掉了 InterruptedException");
            }
            for (int i = 0; i < 5; i++) {
                System.out.print(i + "B ");
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    System.out.println("线程被中断掉了 InterruptedException");
                }
            }
        });
        thread.start();
        thread1.start();
        Thread.sleep(20);
        thread1.interrupt();
    }
}