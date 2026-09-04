package jasper.basic.thread.impl;

public class DemoThread {
    public static void main(String[] args) throws InterruptedException {
        MyThread myThread = new MyThread();
//        myThread.run();
        myThread.start();
        for (int i = 0; i < 5; i++) {
            Thread.sleep(100);
            System.out.println("mainThread");
        }
    }
    public static class MyThread extends Thread{
        @Override
        public void run() {
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            for (int i = 0; i < 5; i++) {
                System.out.println("myThread");
            }
        }
    }

}