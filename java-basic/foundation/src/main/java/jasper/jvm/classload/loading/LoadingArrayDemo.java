package jasper.jvm.classload.loading;

public class LoadingArrayDemo {
    public static class User {
        static {
            System.out.println("init");
        }

        static int a = 10;
    }

    public static void main(String[] args) {
        User[] arr = new User[10];
        System.out.println(User[].class.getName());//[Lcom.jasper.classload.loading.LoadingArrayDemo$User;
    }
}
