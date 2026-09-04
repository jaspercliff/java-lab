package jasper.basic.io.NIO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Server {
    public static void main(String[] args) throws IOException {
        final Selector selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);// 必须是非阻塞，不然selector会一直阻塞到这里
        ssc.bind(new InetSocketAddress(8080));
        ssc.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            // 一直等待，有准备好的channel准备好了操作（读写）返回可操作的通道
            // 该方法是阻塞的，如果没有通道就绪则会将线程挂起
            final int readChannel = selector.select();
            if (readChannel == 0) continue;
            // 所有就绪的通道
            final Set<SelectionKey> selectionKeys = selector.selectedKeys();
            final Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                final SelectionKey key = iterator.next();
                // 必须手动移除 selectionKeys不会自动移除，如果不移除则会导致cpu一直空转
                iterator.remove();
                if (!key.isValid()) continue;
                if (key.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel)key.channel();
                    final SocketChannel clientChannel = server.accept();
                    clientChannel.configureBlocking(false);
                    clientChannel.register(selector, SelectionKey.OP_READ);
                }else if (key.isReadable()) {
                   SocketChannel clientChannel = (SocketChannel) key.channel();
                   ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                    // 先将网络中的数据写入 buffer
                    final int read = clientChannel.read(readBuffer);
                    if (read == -1){
                        clientChannel.close();
                        key.cancel();
                        System.out.println("Client closed");
                    }if (read > 0){
                        readBuffer.flip(); // 切换到读  limit = position  position = 0
                        final String s = new String(readBuffer.array(), 0, readBuffer.limit());
                        System.out.println("Client received: " + s);
                    }

                }
            }
        }
    }
}
