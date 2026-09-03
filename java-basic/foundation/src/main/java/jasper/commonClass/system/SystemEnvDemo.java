package jasper.commonClass.system;

/**
 *  获取环境变量
 */
public class SystemEnvDemo {
    public static void main(String[] args) {
        System.getenv().forEach((k, v)
                -> System.out.println(k + " = " + v));
    }
}
