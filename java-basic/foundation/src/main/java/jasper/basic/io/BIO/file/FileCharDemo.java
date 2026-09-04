package jasper.basic.io.BIO.file;

import lombok.extern.slf4j.Slf4j;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

@Slf4j
public class FileCharDemo {
    public static void main(String[] args) {
//        是基于字符的，它会尝试用默认编码（通常是 UTF-8）来解释字节
        try (final FileReader fileReader = new FileReader("/Users/jasper/IdeaProjects/person/javaLearn/javaBasic/src/main/java/com/jasper/io/doc/bio/source.txt");
             final FileWriter fileWriter = new FileWriter("/Users/jasper/IdeaProjects/person/javaLearn/javaBasic/src/main/java/com/jasper/io/doc/bio/target.txt")
        ) {
            int byteValue;
            while ((byteValue = fileReader.read()) != -1) {
                fileWriter.write(byteValue);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
