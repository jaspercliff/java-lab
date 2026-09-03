package jasper.commonClass.str;

/**
 *   ^ 匹配输入字符串的开始位置   以123开头   不加^  只要任意位置出现 a 都可以 <br>
 *   . 匹配除换行符\n的任意单个字符  <br>
 *   * 匹配前面的子表达式零次或多次        .* 匹配任意字符(前面的任意单个字符)零次或多次 <br>
 */
public class RegDemo {
    public static void main(String[] args) {
        final String constants = "a";
        String reg = "^"+constants+".*";

        final boolean matches = "abc".matches(reg);
        System.out.println("matches = " + matches);
        final boolean matches1 = "bcd".matches(reg);
        System.out.println("matches1 = " + matches1);
    }
}
