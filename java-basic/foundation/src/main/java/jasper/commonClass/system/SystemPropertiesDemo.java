package jasper.commonClass.system;

public class SystemPropertiesDemo {
    public static void main(String[] args) {

        final String property = System.getProperty("name", "default");
        System.out.println("property = " + property);

        System.getProperties().forEach((k, v)
                -> System.out.println(k + " = " + v));


    }
}
