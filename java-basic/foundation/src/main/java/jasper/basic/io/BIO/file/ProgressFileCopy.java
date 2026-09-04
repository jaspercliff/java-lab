package jasper.basic.io.BIO.file;

import javax.swing.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 带进度的文件拷贝
 * 大于1mb 输出进度
 * 时间大于10ms 输出进度
 * 用户可以取消 生成临时文件 然后使用Files.move
 */
public class ProgressFileCopy {
    @FunctionalInterface
    public interface ProgressListener {
        void onProgress(long copied, long total, double percent) throws IOException;
    }

    public static void copyFileWithProgress(String sourcePath, String targetPath, ProgressListener listener) {
        final Path srcPath = Paths.get(sourcePath);
        final Path desPath = Paths.get(targetPath);
        final Path tempPath = Paths.get(targetPath.concat(".temp"));
        try (FileInputStream fis = new FileInputStream(srcPath.toFile());
             FileOutputStream fos = new FileOutputStream(tempPath.toFile());) {
            long totalSize = Files.size(srcPath);
            long copied = 0;
            byte[] bytes = new byte[8192];
            int readBytes;
            long lastUpdate = 0;
            final AtomicBoolean isCancel = new AtomicBoolean(false);
            while ((readBytes = fis.read(bytes)) > 0) {
                if (isCancel.get()) {
                    Files.deleteIfExists(desPath);
                    throw new IOException("copy canceled by user");
                }
                fos.write(bytes, 0, readBytes);
                copied += readBytes;
                // 每读一次就通知
                if (listener != null && copied - lastUpdate > 1024 * 1024) { // 大于1mb打印
                    double percent = (double) copied / totalSize * 100;
                    listener.onProgress(copied, totalSize, percent);
                    lastUpdate = copied;
                }
            }
            fos.close(); // 触发flush 数据肯定落入os缓冲区
            Files.move(tempPath, desPath, StandardCopyOption.REPLACE_EXISTING); // move不关心文件是否真的落盘
            if (listener != null) {
                listener.onProgress(copied, totalSize, 100);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static void main(String[] args) {
        // 50 mb test
        String src = "/Users/jasper/IdeaProjects/person/javaLearn/javaBasic/src/main/java/com/jasper/io/doc/bio/smartsvn-aarch64-14_5_0.dmg";
        String dest = "/Users/jasper/IdeaProjects/person/javaLearn/javaBasic/src/main/java/com/jasper/io/doc/bio/test.dmg";
        long start = System.currentTimeMillis();
        copyFileWithProgress(src, dest, ((copied, total, percent) -> {
            long elapsed = (System.currentTimeMillis() - start) / 10;  // 控制输出频率
            if (elapsed > 0) {
                final String copiedSize = formatByte(copied);
                final String totalSize = formatByte(total);
                System.out.printf("\r copying: %s/%s  percent: %.2f \n", copiedSize, totalSize, percent);
            }
        }));
    }

    /**
     * 格式化 字节 kb mb gb
     *
     * @param bytes 字节
     * @return kb mb gb  可能有误差 浮点数计算误差
     */
    public static String formatByte(long bytes) {
        if (bytes < 1024) {
            return bytes + "B";
        }
        int exp = (int) (Math.log(bytes) / Math.log(1024)); // 可能约等于1.9999999 int之后变为1
        char unit = "KMGTPE".charAt(exp - 1);
        double size = bytes / Math.pow(1024, exp);
        return String.format("%.1f %cB", size, unit);
    }

}
