package jasper.juc.volatileDemo;

/**
 * synchronized也可以解决可见性的问题
 */
public class VolatileDemo2 {
    private static  boolean flag = true;
    public static synchronized void setFlag(boolean flagValue) {
        flag = flagValue;
    }

    public static synchronized boolean getFlag() {
        return flag;
    }
    public static void main(String[] args) throws InterruptedException {
        new Thread(() -> {
            int i = 0;
            while (getFlag()) {
                // 循环体故意留空或执行非内存同步操作
                i++;
            }
            System.out.println("Thread stops with i=" + i);
        }).start();

        Thread.sleep(1000); // 确保线程启动并运行
        System.out.println("main thread changes flag to false");
        setFlag(false);
    }
}

