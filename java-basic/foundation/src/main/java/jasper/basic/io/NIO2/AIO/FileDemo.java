package jasper.basic.io.NIO2.AIO;

import com.jasper.io.Constants;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

@Slf4j
public class FileDemo {
    public static final String STRING_TO_WRITE = "THIS IS ASYNCHRONOUS FILE DATA TO WRITE";
    public static void main(String[] args) throws IOException, InterruptedException {
        final Path path = Paths.get(Constants.COMMON_PATH);
        final AsynchronousFileChannel asyncFileChannel = AsynchronousFileChannel.open(path, StandardOpenOption.CREATE,
                StandardOpenOption.WRITE,
                StandardOpenOption.TRUNCATE_EXISTING);
        final ByteBuffer buffer = ByteBuffer.wrap(STRING_TO_WRITE.getBytes(StandardCharsets.UTF_8));
        asyncFileChannel.write(buffer,0,asyncFileChannel,new CompletionHandler<Integer, AsynchronousFileChannel>() {
            @Override
            public void completed(final Integer result, final AsynchronousFileChannel attachment) {
                System.out.println("Writing completed asynchronous file... with result bytes written " + result);
                try {
                    attachment.close();
                    asyncRead(path);
                } catch (IOException e) {
                    log.error(e.getMessage());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void failed(final Throwable exc, final AsynchronousFileChannel attachment) {
                if (attachment!=null && attachment.isOpen()) {
                    try {
                        attachment.close();
                    } catch (IOException e) {
                        log.info(exc.getMessage());
                    }
                }
            }
        });

        Thread.sleep(2000);
        System.out.println("Writing asynchronous file...");

    }


    public static void asyncRead(Path path) throws IOException, InterruptedException {
        final AsynchronousFileChannel readChannel = AsynchronousFileChannel.open(path, StandardOpenOption.READ);
        final long size = readChannel.size();
        ByteBuffer buffer = ByteBuffer.allocate((int) size);
        readChannel.read(buffer,0,new Object[]{readChannel,buffer},new CompletionHandler<Integer, Object[]>() {

            @Override
            public void completed(final Integer result, final Object[] attachment) {
                ByteBuffer byteBuffer = (ByteBuffer) attachment[1];
                AsynchronousFileChannel readChannel = (AsynchronousFileChannel) attachment[0];
                byteBuffer.flip();
                final String content = StandardCharsets.UTF_8.decode(byteBuffer).toString();
                System.out.println("read date is ".concat(content));
                try {
                    readChannel.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void failed(final Throwable exc, final Object[] attachment) {
                AsynchronousFileChannel writeChannel = (AsynchronousFileChannel) attachment[0];
                try {
                    writeChannel.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
