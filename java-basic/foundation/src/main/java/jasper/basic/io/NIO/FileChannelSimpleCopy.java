package jasper.basic.io.NIO;

import com.jasper.io.Constants;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class FileChannelSimpleCopy {
    public static void main(String[] args) throws IOException {
        final Path srcPath = Paths.get(Constants.NIO_FILE_SOURCE_PATH);
        final Path desPath = Paths.get(Constants.NIO_FILE_TARGET_PATH);
        copy(srcPath,desPath,(progress,transferred,total)->{
            System.out.println(progress);
            System.out.println(transferred);
            System.out.println(total);
        });

    }

    @FunctionalInterface
    public interface listener {
        void onProgress(long progress, long transferred, long totalSize);
    }

    public static void copy(Path srcPath, Path desPath, listener listener) throws IOException {

        try (FileChannel srcChannel = FileChannel.open(srcPath, StandardOpenOption.READ);
             FileChannel desChannel = FileChannel.open(desPath, StandardOpenOption.WRITE, StandardOpenOption.CREATE)
        ) {
            final long totalSize = srcChannel.size();
            long transferred = 0;
            final ByteBuffer byteBuffer = ByteBuffer.allocate(2);
            while (transferred < totalSize) {
                byteBuffer.clear();
                final int readByte = srcChannel.read(byteBuffer);
                while (readByte == -1) {
                    break;
                }
                // limit = position  position = 0
                byteBuffer.flip();
                final int writeByte = desChannel.write(byteBuffer);
                transferred += writeByte;
                long progress = transferred * 100 / totalSize;
                if (listener != null) {
                    listener.onProgress(progress, transferred, totalSize);
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
