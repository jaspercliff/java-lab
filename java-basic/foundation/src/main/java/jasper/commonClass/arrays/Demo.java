package jasper.commonClass.arrays;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings({"unused"})
public class Demo {

    public static void main(String[] args) {
        int[] a = {1, 2, 3, 4, 5};
        int[] b = Arrays.copyOf(a, 5);
        System.out.println(Arrays.toString(b));
        // Arrays.asList 无法直接处理 int[]，它会把你得到 List<int[]> 而不是 List<Integer>
        ArrayList<int[]> aList = new ArrayList<>(Arrays.asList(a));

        List<Integer> collect = Arrays.stream(a).boxed().collect(Collectors.toList());
        System.out.println(collect);
    }
}
