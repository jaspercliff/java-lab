package jasper.commonClass.str;

/**
 * @author jasper
 * @since 2026-05-05 11:14:09
 */
public class SubStringDemo {
    public static void main(String[] args) {
        String s = "hello world";
        //   从6到结尾
        String substring = s.substring(6);
        System.out.println("substring = " + substring); // world
        //  左闭右开
        String substring2 = s.substring(0, 4);
        System.out.println("substring2 = " + substring2); // hell
    }
}
