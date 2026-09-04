package jasper.basic.exception;

/**
 * @author jasper
 * @since 2026-07-16 21:02:14 <br>
 */
public class ResourceDemo {
    public static void main(String[] args) {

        // jdk9 feature final resource  可以直接在try中写变量而不用重新赋值
        final MyResource resource = new MyResource();
        try (MyResource1 resource1 = new MyResource1();
                resource) {
            resource1.doSome();
            resource.doSome();

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
