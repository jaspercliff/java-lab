package jasper.commonClass.system;

/**
 * -Dresilience4j.thread.type=virtual 配置JVM 的系统属性
 * idea vm options
 */
public class SystemPropertiesDemo {
    public static void main(String[] args) {

        final String property = System.getProperty("resilience4j.thread.type", "default");
        System.out.println("property = " + property);

        String property1 = System.getProperty("test.name");
        System.out.println("property1 = " + property1);
        if (property1 == null) {
            System.setProperty("test.name", "jasper");
        }
        String property2 = System.getProperty("test.name");
        System.out.println("property2 = " + property2);

        System.getProperties().forEach((k, v)
                -> System.out.println(k + " = " + v));
    }
}
