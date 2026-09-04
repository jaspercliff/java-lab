package jasper.basic.io.BIO.buffer;

import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
public class BufferedCharDemo {
    public static void main(String[] args) {
//        是基于字符的，它会尝试用默认编码（通常是 UTF-8）来解释字节
        try (final FileReader fileReader = new FileReader("/Users/jasper/IdeaProjects/person/javaLearn/javaBasic/src/main/java/com/jasper/io/doc/source.txt");
             final FileWriter fileWriter = new FileWriter("/Users/jasper/IdeaProjects/person/javaLearn/javaBasic/src/main/java/com/jasper/io/doc/target.txt");
             final BufferedReader bufferedReader = new BufferedReader(fileReader);
             final BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)
        ) {
            int byteValue;
            while ((byteValue = bufferedReader.read()) != -1) {
                bufferedWriter.write(byteValue);
//                数据先写到内存缓冲区 不会立即写到磁盘或者网络上，只有缓冲区慢了或者流关闭的时候才会写入，使用flush可以手动刷新缓冲区，立刻写入文件
                bufferedWriter.flush();
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
