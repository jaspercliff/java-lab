package jasper.jvm.memory.object;


public class JolDemo {

    static class User {
        int id; // 4 字节
        boolean isVip; // 1 字节
        String name; // 引用类型：开启指针压缩占 4 字节，关闭占 8 字节
    }

    public static void main(String[] args) {
        User user = new User();

        // 核心：打印 user 对象的内存布局
//        System.out.println(ClassLayout.parseInstance(user).toPrintable());
    }
}
