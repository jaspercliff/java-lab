package jasper.commonClass.str;

public class SplitDemo {
    public static void main(String[] args) {
        String name="admin=123,1,34";
//        s = admin=123
//        s = 1,34             2 是分割最多俩次，后面的都组成一个
        final String[] split = name.split(",", 2);
        for (final String s : split) {
            System.out.println("s = " + s);
        }
    }
}
