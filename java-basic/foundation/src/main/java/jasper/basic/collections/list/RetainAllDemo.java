package jasper.basic.collections.list;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @Author jasper @Date 2024-12-13
 *
 * @version 1.0 @Description
 */
public class RetainAllDemo {
  public static void main(String[] args) {
    ArrayList<Integer> list1 = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5));
    ArrayList<Integer> list2 = new ArrayList<>(Arrays.asList(3, 4, 5, 6));
    // list1 中只会保留那些同时存在于 list2 中的元素
    list1.retainAll(list2);
    System.out.println("list1 = " + list1);
  }
}
