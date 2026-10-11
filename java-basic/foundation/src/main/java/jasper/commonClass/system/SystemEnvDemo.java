package jasper.commonClass.system;

/**
 * export RESILIENCE4J_THREAD_TYPE=virtual
 *  获取环境变量
 *  idea  Environment variables
 */
public class SystemEnvDemo {
    public static void main(String[] args) {
        System.getenv().forEach((k, v)
                -> System.out.println(k + " = " + v));
    }
}
