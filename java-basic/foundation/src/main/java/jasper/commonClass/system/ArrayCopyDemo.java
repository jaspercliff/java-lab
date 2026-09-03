package jasper.commonClass.system;

import java.util.Arrays;

public class ArrayCopyDemo {
    public static void main(String[] args) {
        int[] a = {1, 2, 3, 4, 5};
        int[] b = new int[5];
        System.arraycopy(a, 1, b, 0, 3);
        System.out.println(Arrays.toString(b));
    }
}
