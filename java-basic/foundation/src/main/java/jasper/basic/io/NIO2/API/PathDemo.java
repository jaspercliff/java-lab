package jasper.basic.io.NIO2.API;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

public class PathDemo {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        final Path path = Paths.get("/Users/jasper/IdeaProjects/person/javaLearn/javaBasic/src/main/java/com/jasper/io/test.properties");

        if (path.toFile().exists()) {
            try(final FileInputStream fileInputStream = new FileInputStream(path.toFile())) {
                final Properties properties = new Properties();
                properties.load(fileInputStream);
                final String name = properties.getProperty("name");
                System.out.println("name = " + name);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        // 将指定路径的文件全部读取为一个字节数组
        final byte[] bytes = Files.readAllBytes(path);

        final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
//          提供数据
        messageDigest.update(bytes);
//        计算hash
        final byte[] digest = messageDigest.digest();

    }
}
