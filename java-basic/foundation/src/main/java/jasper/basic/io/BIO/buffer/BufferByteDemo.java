package jasper.basic.io.BIO.buffer;

import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
public class BufferByteDemo {
    public static void main(String[] args) {
        try (FileInputStream fileInputStream = new FileInputStream("/Users/jasper/IdeaProjects/person/javaLearn/javaBasic/src/main/java/com/jasper/io/doc/1.jpeg");
             final FileOutputStream fileOutputStream = new FileOutputStream("/Users/jasper/IdeaProjects/person/javaLearn/javaBasic/src/main/java/com/jasper/io/doc/2.jpeg");
             final BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
             final BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream)
        ) {
            int byteRead;
            while ((byteRead = bufferedInputStream.read()) != -1) {
                bufferedOutputStream.write(byteRead);
//                数据先写到内存缓冲区 不会立即写到磁盘或者网络上，只有缓冲区慢了或者流关闭的时候才会写入，使用flush可以手动刷新缓冲区，立刻写入文件
                bufferedOutputStream.flush();
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
