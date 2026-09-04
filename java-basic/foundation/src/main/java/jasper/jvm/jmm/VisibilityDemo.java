package jasper.jvm.jmm;

public class VisibilityDemo {
    // If volatile is not added, the modification by the main thread may be invisible to Thread 1
    private static volatile boolean flag = true;

    public static void main(String[] args) throws InterruptedException {
        new Thread(
                        () -> {
                            while (flag) {
                                // The execution engine continuously executes 'use', always reading
                                // the old copy in working memory
                            }
                            System.out.println("Thread 1 stopped.");
                        })
                .start();

        Thread.sleep(100); //  Ensure Thread 1 is up and running
        flag = false; //  Triggers 'assign' operation
        System.out.println("Main thread changed flag to false.");
    }
}
