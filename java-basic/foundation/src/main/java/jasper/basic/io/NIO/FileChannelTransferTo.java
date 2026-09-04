package jasper.basic.io.NIO;

import com.jasper.io.Constants;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.channels.FileChannel;
import java.nio.channels.SocketChannel;

public class FileChannelTransferTo {
    public static void main(String[] args) throws IOException {
        final RandomAccessFile file = new RandomAccessFile(Constants.NIO_FILE_SOURCE_PATH, "r");

        final SocketChannel clientChannel = SocketChannel.open();
        clientChannel.connect(new InetSocketAddress(Constants.IP, Constants.PORT));
        FileChannel fileChannel = file.getChannel();
        try (file; fileChannel; clientChannel) {
            long position = 0;
            long count = fileChannel.size();
            long transferred = 0;

            while (count >0) {
                transferred  = fileChannel.transferTo(position, count , clientChannel);
                position += transferred;
                count -= transferred;
            }
        }
    }
}
