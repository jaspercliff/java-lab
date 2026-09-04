package jasper.jvm.classload.loading;

public class LoadingDemo {

    public static class User {
        static {
            System.out.println("init");
        }

        static int a = 10;
    }

    /*
    ClassLoader.loadClass() 和 Class.forName(name, false, loader) 只会触发类加载（Loading），不会执行初始化；
    只有在主动使用类（如访问静态变量）时，才会触发 Initialization
     */
    public static void main(String[] args) throws Exception {

        System.out.println("===== 方式1：loadClass =====");
        ClassLoader loader = LoadingDemo.class.getClassLoader();
        //Java 编译后，内部类的“真实类名”就是用 $ 表示的
        Class<?> clazz1 = loader.loadClass("com.jasper.classload.loading.LoadingDemo$User");

        System.out.println("加载完成，但没有初始化");
        System.out.println(clazz1);

        System.out.println("\n===== 方式2：forName(false) =====");
        Class<?> clazz2 = Class.forName("com.jasper.classload.loading.LoadingDemo$User", false,  // 不初始化
                loader);

        System.out.println("加载完成，但没有初始化");
        System.out.println(clazz2);

        System.out.println("\n===== 主动触发初始化 =====");
        // 触发初始化
        System.out.println(User.a);
    }
}