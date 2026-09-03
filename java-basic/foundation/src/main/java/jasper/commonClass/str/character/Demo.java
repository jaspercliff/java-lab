package jasper.commonClass.str.character;

/**
 * @author jasper
 * @since 2026-04-30 10:46:17
 */
public class Demo {
    public static void main(String[] args) {
        String word = "USA123";
        char[] charArray = word.toCharArray();
        // 大写
        boolean upperCase = Character.isUpperCase(charArray[1]);
        System.out.println("upperCase = " + upperCase);
        // 小写
        boolean lowerCase = Character.isLowerCase(charArray[0]);
        System.out.println("lowerCase = " + lowerCase);
        int count = 0;
        for (char c : word.toCharArray()) {
            // 是否数字
            if (Character.isDigit(c)) count++;
        }
        System.out.println("count " + count);
    }
}
