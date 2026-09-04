package jasper.basic.io.NIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class Client {
    public static void main(String[] args) throws IOException {
        // 打开 SocketChannel
        SocketChannel socketChannel = SocketChannel.open();
        // 连接服务器
        socketChannel.connect(new InetSocketAddress("127.0.0.1", 8080));

        // 构造要发送的数据
        String msg = "Hello NIO Server!";
        ByteBuffer buffer = ByteBuffer.wrap(msg.getBytes());

        // 发送消息
        socketChannel.write(buffer);
        System.out.println("Client sent: " + msg);

        // 关闭连接
        socketChannel.close();
    }
}
