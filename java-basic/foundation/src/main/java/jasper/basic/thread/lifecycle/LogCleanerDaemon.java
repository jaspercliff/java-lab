package jasper.basic.thread.lifecycle;

public class LogCleanerDaemon {
    public static void main(String[] args) {
// 创建一个守护线程
        Thread logCleanerThread = new Thread(() -> {
            System.out.println("日志清理守护线程启动");

            while (true) {
                try {
                    // 模拟清理日志文件的操作
                    cleanLogs();
                    // 每隔10秒钟执行一次清理
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    // 处理中断异常
                    e.printStackTrace();
                }
            }
        });

        // 设置为守护线程
        logCleanerThread.setDaemon(true);
        // 启动守护线程
        logCleanerThread.start();

        // 模拟应用程序的其他操作
        try {
            System.out.println("应用程序正在运行");
            Thread.sleep(30000); // 模拟应用程序运行了一段时间后结束
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("应用程序结束");
        // 应用程序结束时，守护线程也会自动结束
    }

    private static void cleanLogs() {
        // 这里只是打印消息模拟清理日志文件的操作
        System.out.println("清理旧的日志文件...");
        // 实际应用中，这里应该添加清理日志文件的代码
    }
}