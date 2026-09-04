package jasper.basic.io.NIO2.API;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

/**
 */
public class FileCopyDemo {
    public static void main(String[] args) throws IOException {
        String srcPath = "/Users/jasper/IdeaProjects/person/javaLearn/javaBasic/src/main/java/com/jasper/io/doc/files/a.txt";
        String destPath = "/Users/jasper/IdeaProjects/person/javaLearn/javaBasic/src/main/java/com/jasper/io/doc/filesCopy/";
        copyFile(srcPath,destPath);

        String sourceDir = "/Users/jasper/IdeaProjects/person/javaLearn/javaBasic/src/main/java/com/jasper/io/doc/files";
        String targetDir = "/Users/jasper/IdeaProjects/person/javaLearn/javaBasic/src/main/java/com/jasper/io/doc/filesCopy1/";

        // Files.copy() 不会递归复制目录中的内容。要复制整个目录树，你需要手动遍历目录结构或使用递归方法。
        Files.copy(Paths.get(sourceDir),Paths.get(targetDir) , StandardCopyOption.REPLACE_EXISTING);

    }

    public static void copyFile(String srcPath, String desPath) {
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
            throw new RuntimeException("Failed to create destination directory: ".concat(destDir.toString()), e);
        }
        // 执行复制
        try {
            Files.copy(src, destFile, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to copy file from ".concat(src.toString()).concat(" to ").concat(destFile.toString()), e);
        }
    }
}
