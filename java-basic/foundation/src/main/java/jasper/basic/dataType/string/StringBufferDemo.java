package jasper.basic.dataType.string;

/**
 * @author jasper
 * @since 2026-05-19 15:41:37
 */
public class StringBufferDemo {
    public static void main(String[] args) {}

    public String createString(String s1, String s2) {
        StringBuffer sb = new StringBuffer(); // StringBuffer 内部的方法全都有 synchronized
        sb.append(s1);
        sb.append(s2);
        return sb.toString();
    }
}
