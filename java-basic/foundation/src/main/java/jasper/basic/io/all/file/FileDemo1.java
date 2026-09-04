package jasper.basic.io.all.file;

import java.io.File;
import java.util.Arrays;

/**
 * @Author jasper @Date 2024-11-18
 *
 * @version 1.0
 */
public class FileDemo1 {
  public static void main(String[] args) {
    File directory =
        new File("C:\\code\\javaLearn\\01javaBasic\\src\\main\\java\\com\\jasper\\file");
    File[] files = directory.listFiles();
    Arrays.stream(files).skip(2).forEach(File::delete);
  }
}
