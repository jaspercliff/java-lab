package jasper.basic.thread.lifecycle;

public class WaitDemo {
    public static void main(String[] args) {
        Object monitor = new Object();
        new Thread(() -> {
            synchronized (monitor){
                try {
                    // 会让当前线程释放锁
                    monitor.wait();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    System.out.println("before");
                }
            }
        }).start();

        /*
         * monitor.notify() 会唤醒一个等待中的线程，但不会立即让该线程重新获取锁。只有当前线程（线程 2）释放锁后，被唤醒的线程（线程 1）才能重新获取锁并继续执行
         */
        new Thread(() -> {
            synchronized (monitor){
                monitor.notify();
                System.out.println("after");
            }
        }).start();
    }
}
