package jasper.basic.io.NIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Set;

public class ChatServer {
    private static final int PORT = 8080;
    private static final Charset CHARSET = Charset.forName("UTF-8");
    private Selector selector;
    private ServerSocketChannel serverChannel;

    public void start() throws IOException {
        // 1. 创建 Selector
        selector = Selector.open();

        // 2. 创建 ServerSocketChannel (用于监听端口)
        serverChannel = ServerSocketChannel.open();
        serverChannel.configureBlocking(false); // 设置为非阻塞
        serverChannel.bind(new InetSocketAddress(PORT));

        // 3. 将 ServerSocketChannel 注册到 Selector，监听 "ACCEPT" 事件
        serverChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("Chat Server started on port " + PORT);

        while (true) {
            // 4. 阻塞等待，直到有 Channel 就绪 (有新连接或有数据可读)
            int readyChannels = selector.select();
            if (readyChannels == 0) continue; // 没有就绪的 Channel，继续循环

            // 5. 获取就绪的 SelectionKey 集合
            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                keyIterator.remove(); // 必须移除，否则下次 select 还会包含它

                try {
                    if (key.isAcceptable()) {
                        // 6. 处理新连接 (ACCEPT 事件)
                        handleAccept(key);
                    } else if (key.isReadable()) {
                        // 7. 处理客户端发送的数据 (READ 事件)
                        handleRead(key);
                    }
                } catch (IOException e) {
                    System.out.println("Client disconnected: " + e.getMessage());
                    // 客户端断开连接，关闭 Channel 和 Key
                    key.cancel();
                    try {
                        key.channel().close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

    private void handleAccept(SelectionKey key) throws IOException {
        ServerSocketChannel serverChannel = (ServerSocketChannel) key.channel();
        // 接受新连接，得到与客户端通信的 SocketChannel
        SocketChannel clientChannel = serverChannel.accept();
        clientChannel.configureBlocking(false); // 设置为非阻塞

        // 将新的客户端 Channel 注册到同一个 Selector，监听 "READ" 事件
        clientChannel.register(selector, SelectionKey.OP_READ);
        System.out.println("New client connected: " + clientChannel.getRemoteAddress());
    }

    private void handleRead(SelectionKey key) throws IOException {
        SocketChannel clientChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        StringBuilder message = new StringBuilder();

        try {
            int bytesRead;
            while ((bytesRead = clientChannel.read(buffer)) > 0) {
                buffer.flip(); // 切换到读模式
                message.append(CHARSET.decode(buffer));
                buffer.clear(); // 清空，准备下一次读取
            }

            if (bytesRead == -1) {
                // 客户端关闭了连接
                System.out.println("Client closed connection: " + clientChannel.getRemoteAddress());
                key.cancel();
                clientChannel.close();
                return;
            }

            String msg = message.toString().trim();
            if (!msg.isEmpty()) {
                System.out.println("Received from " + clientChannel.getRemoteAddress() + ": " + msg);
                // 8. 广播消息给所有其他客户端 (除了发送者)
                broadcastMessage(msg, clientChannel);
            }
        } catch (IOException e) {
            // 读取过程中发生错误，视为客户端断开
            key.cancel();
            clientChannel.close();
            throw e;
        }
    }

    private void broadcastMessage(String message, SocketChannel sender) throws IOException {
        // 获取所有已注册的 Channel
        for (SelectionKey key : selector.keys()) {
            Channel channel = key.channel();
            if (channel instanceof SocketChannel && channel != sender) {
                SocketChannel targetChannel = (SocketChannel) channel;
                // 简化：直接写入。实际应用中可能需要处理 WRITE 事件和缓冲区满的情况。
                ByteBuffer buffer = CHARSET.encode("[" + sender.getRemoteAddress() + "]: " + message);
                targetChannel.write(buffer);
            }
        }
    }

    public static void main(String[] args) {
        try {
            new ChatServer().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}