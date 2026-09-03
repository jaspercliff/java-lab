package jasper.commonClass.system;

/**
 * @author jasper
 * @since 2026-05-12 17:54:27
 */
public class RuntimeDemo {
    public static void main(String[] args) {
        Runtime runtime = Runtime.getRuntime();
        int availableProcessors = runtime.availableProcessors();
        System.out.println("availableProcessors = " + availableProcessors);
        long maxMemory = runtime.maxMemory();
        System.out.println("maxMemory = " + maxMemory);
        long totalMemory = runtime.totalMemory();
        System.out.println("totalMemory = " + totalMemory);
        long freeMemory = runtime.freeMemory();
        System.out.println("freeMemory = " + freeMemory);
        // runtime.exec("ls");
    }
}
