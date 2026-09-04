package jasper.juc.unsafe.memorySegement;

//import java.lang.foreign.Arena;
//import java.lang.foreign.MemorySegment;
//import java.lang.foreign.ValueLayout;

/**
 * @author jasper
 * @since 2026-04-30 15:17:33 java --enable-preview --source 21
 *     /Users/jasper/code/java/person/learnJava/basic/juc/src/main/java/com/jasper/unsafe/memorySegement/SegmentDemo.java
 */
public class SegmentDemo {
    public static void main(String[] args) {
        // 1. Arena (竞技场) 负责管理内存的生命周期
        // Arena.ofConfined()：单线程使用，性能最高，随代码块结束释放。
        // Arena.ofShared()：多线程共享。
        // Arena.global()：跟虚拟机同寿，不释放。
        // 使用 try-with-resources，执行完后内存自动释放！
//        try (Arena arena = Arena.ofConfined()) {

            // 2. 申请一段 8 字节的内存段
            // 内部自动处理了类似 allocateMemory(8) 的逻辑
//            MemorySegment segment = arena.allocate(8);

            // 3. 写入数据
            // ValueLayout.JAVA_LONG 明确了这是 8 字节，且处理了字节序
//            segment.set(ValueLayout.JAVA_LONG, 0, 123456L);

            // 4. 读取数据
//            long value = segment.get(ValueLayout.JAVA_LONG, 0);

//            System.out.println("Value = " + value);
//            System.out.println("Address = " + segment.address());
//            System.out.println("Size = " + segment.byteSize());
//        } // <--- 这里 arena 关闭，内存自动 free，绝无泄漏！
    }
}
