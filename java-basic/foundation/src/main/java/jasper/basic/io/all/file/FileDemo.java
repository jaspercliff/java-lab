package jasper.basic.io.all.file;

import java.io.File;
import java.io.IOException;

/**
 * @Author jasper @Date 2024-11-18
 *
 * @version 1.0
 */
public class FileDemo {
  public static void main(String[] args) throws IOException {
    File directory = new File("C:\\code\\javaLearn\\01javaBasic\\src\\main\\java\\com\\jasper\\file");
    boolean isDirectory = directory.isDirectory();
    System.out.println("isDirectory = " + isDirectory);
    File file = new File(directory, "text1.txt");
    boolean isFile = file.isFile();
    System.out.println("isFile = " + isFile);
    boolean isCreat = file.createNewFile();
    System.out.println("isCreat = " + isCreat);
  }
}
