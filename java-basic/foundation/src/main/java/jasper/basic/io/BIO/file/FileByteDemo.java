package jasper.basic.io.BIO.file;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@Slf4j
public class FileByteDemo {
    public static void main(String[] args) {
        try (FileInputStream fileInputStream = new FileInputStream("/Users/jasper/IdeaProjects/person/javaLearn/javaBasic/src/main/java/com/jasper/io/doc/bio/1.jpeg");
             final FileOutputStream fileOutputStream = new FileOutputStream("/Users/jasper/IdeaProjects/person/javaLearn/javaBasic/src/main/java/com/jasper/io/doc/bio/2.jpeg")
        ) {
            int byteRead;
            while ((byteRead = fileInputStream.read()) != -1) {
                fileOutputStream.write(byteRead);
            }
        }catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
