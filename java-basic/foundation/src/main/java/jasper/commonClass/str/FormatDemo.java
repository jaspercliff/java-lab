package jasper.commonClass.str;

/**
 * @Author jasper @Date 2024-11-18
 *
 * @version 1.0
 */
public class FormatDemo {
  public static void main(String[] args) {
    String jasper = String.format("name is %s and age is %s", "jasper", "20");
    System.out.println("jasper = " + jasper);

    String format = String.format("%04d", 123);
    System.out.println("format = " + format);
    String substring = format.substring(0, 1);
    System.out.println("substring = " + substring);

  }
}
