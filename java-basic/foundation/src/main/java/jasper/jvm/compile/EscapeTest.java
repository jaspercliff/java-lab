package jasper.jvm.compile;

/**
 * java -Xmx15m -Xms15m -XX:-DoEscapeAnalysis -XX:-EliminateAllocations -XX:+PrintGCDetails
 * EscapeTest <br>
 * 逃逸分析 标量替换
 */
public class EscapeTest {
    public static void main(String[] args) throws Exception {
        // 循环 1 亿次，创建 1 亿个没有逃逸的 Point 对象
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100_000_000; i++) {
            allocate();
        }
        long end = System.currentTimeMillis();
        System.out.println("耗时: " + (end - start) + " ms");
    }

    private static void allocate() {
        Point point = new Point(1, 2); // 绝对不逃逸
    }

    static class Point {
        int x;
        int y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
}
