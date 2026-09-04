package jasper.basic.io.NIO;

import java.nio.ByteBuffer;

/**
 * -XX:NativeMemoryTracking=detail -XX:MaxDirectMemorySize=128M 限制最大直接内存 jcmd pid VM.native_memory
 * summary
 *
 * <p>arthas memory direct 12M 12M 100.00%
 */
public class DirectByteBufferDemo {
    public static void main(String[] args) throws InterruptedException {
        ByteBuffer directByteBuffer = ByteBuffer.allocateDirect(1024 * 8 * 1024);
        String data = "hello direct byte buffer";
        directByteBuffer.put(data.getBytes());
        directByteBuffer.flip(); // 切换为读模式
        byte[] read = new byte[directByteBuffer.remaining()];
        directByteBuffer.get(read);
        System.out.println("read = " + new String(read));
        Thread.sleep(50000);
    }
}
