package jasper.jvm.gc.reference;

import java.lang.ref.SoftReference;

// -Xmx20m -Xlog:'gc*'
public class SoftRefDemo {
    // 内存不足时会回收软引用 适合缓存(现在不推荐使用 recommend：caffeine redis)
    public static void main(String[] args) {
        // 创建一个 10MB 的软引用对象
        SoftReference<byte[]> softRef = new SoftReference<>(new byte[1024 * 1024 * 10]);

        System.out.println("内存充足时: " + softRef.get()); // 能拿到对象

        try {
            // 制造一个巨大的强引用对象，强行把内存撑爆
            byte[] leak = new byte[1024 * 1024 * 15];
        } finally {
            // 再次查看软引用
            System.out.println("内存不足后软引用: " + softRef.get()); // 结果：null
        }
    }
}
