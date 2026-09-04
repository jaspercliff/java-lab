// -Xms50m -Xmx50m -XX:+HeapDumpOnOutOfMemoryError
// -XX:HeapDumpPath=/Users/jasper/workspace/dump/heapdump.hprof
package jasper.jvm.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author jasper
 * @since 2026-07-01 20:02:40 <br>
 */
public class MemoryLeak {
    // 静态集合，生命周期极长
    private static final List<DataHolder> registry = new ArrayList<>();

    public static void main(String[] args) throws InterruptedException {
        System.out.println("开始往静态集合塞数据...");
        while (true) {
            registry.add(new DataHolder(UUID.randomUUID().toString()));
            // 模拟业务间隔，防止瞬间崩掉
            Thread.sleep(1);
        }
    }

    static class DataHolder {
        private String id;
        private byte[] largeBuffer = new byte[1024 * 10]; // 每个对象带10KB数据

        public DataHolder(String id) {
            this.id = id;
        }
    }
}
