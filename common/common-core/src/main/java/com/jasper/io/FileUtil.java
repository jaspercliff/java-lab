package com.jasper.io;

import com.jasper.lang.ObjectUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileUtil {

    /**
     * 创建指定路径的目录。
     *
     * @param filePath 目录路径
     */
    public static void mkdirs(String filePath) {
        if (ObjectUtils.isNotNull(filePath)) {
            File file = new File(filePath);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
    }

    /**
     * @param file file
     * @return 文件行数
     */
    public static int getFileLines(File file) {
        try (var lines = Files.lines(file.toPath())) {
            return (int) lines.count(); // 🚀 优雅、安全，在大文件下绝不 OOM
        } catch (IOException e) {
            throw new RuntimeException("Failed to count lines for file: " + file.getName(), e);
        }
    }

    /**
     * 复制文件到指定目录。 使用AIO Files.copy 底层使用零拷贝，基于系统内核级别的，如果不支持，则由jvm选择 FileChannel.transferTo
     * MappedByteBuffer
     *
     * @param srcPath 源文件路径
     * @param desPath 目标目录路径
     * @throws IllegalArgumentException 如果源或目标路径为空
     * @throws RuntimeException 复制过程中发生IO异常
     */
    public static void copyFile(String srcPath, String desPath) {
        {
            if (srcPath == null || srcPath.trim().isEmpty()) {
                throw new IllegalArgumentException("Source path cannot be null or empty");
            }
            if (desPath == null || desPath.trim().isEmpty()) {
                throw new IllegalArgumentException("Destination path cannot be null or empty");
            }
            Path src = Paths.get(srcPath.trim());
            Path destDir = Paths.get(desPath.trim());
            Path destFile = destDir.resolve(src.getFileName());
            // 创建目标目录（如果不存在）
            try {
                Files.createDirectories(destDir);
            } catch (IOException e) {
                throw new RuntimeException(
                        "Failed to create destination directory: ".concat(destDir.toString()), e);
            }
            // 执行复制
            try {
                Files.copy(src, destFile, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
                throw new RuntimeException(
                        "Failed to copy file from "
                                .concat(src.toString())
                                .concat(" to ")
                                .concat(destFile.toString()),
                        e);
            }
        }
    }

    /**
     * 复制文件到指定目录。 使用字节流
     *
     * @param srcPath 源文件路径
     * @param desPath 目标目录路径
     * @throws IllegalArgumentException 如果源或目标路径为空
     * @throws RuntimeException 复制过程中发生IO异常
     */
    public static void copyFileStream(String srcPath, String desPath) {
        final String desName = new File(srcPath).getName();
        mkdirs(desPath);
        desPath = desPath + File.separator + desName;
        try (FileInputStream fileInputStream = new FileInputStream(srcPath);
                FileOutputStream fileOutputStream = new FileOutputStream(desPath); ) {
            byte[] bytes = new byte[1024 * 8];
            int byteRead;
            while ((byteRead = fileInputStream.read(bytes)) != -1) {
                fileOutputStream.write(bytes, 0, byteRead);
            }
        } catch (IOException ioException) {
            throw new RuntimeException(ioException.getMessage());
        }
    }
}
