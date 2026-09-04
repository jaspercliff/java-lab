package jasper.jvm.compile;

public class StringBufferLockEliminationTest {
    public static void main(String[] args) {
        // 先让 JVM 热热身，确保连续调用触发 JIT 即时编译
        for (int i = 0; i < 10_000; i++) {
            concatString("a", "b");
        }

        // 开始正式测试：循环 5000 万次
        long start = System.currentTimeMillis();
        for (int i = 0; i < 50_000_000; i++) {
            concatString("hello-", "world");
        }
        long end = System.currentTimeMillis();

        System.out.println("耗时: " + (end - start) + " ms");
    }

    private static String concatString(String s1, String s2) {
        // sb 是方法内部的局部变量，且没有逃逸出这个方法
        StringBuffer sb = new StringBuffer();

        // StringBuffer 的 append 方法源码内部带有 synchronized 关键字
        // 逃逸分析发现 sb 不逃逸，JIT 会在编译时直接把这里的锁全部“消除”
        sb.append(s1);
        sb.append(s2);

        return sb.toString();
    }
}
