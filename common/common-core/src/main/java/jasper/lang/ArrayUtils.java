package jasper.lang;

public final class ArrayUtils {

    private ArrayUtils() {
    }

    public static <T> boolean isEmpty(T[] array) {
        return array == null || array.length == 0;
    }

    public static <T> boolean isNotEmpty(T[] array) {
        return !isEmpty(array);
    }


    public static <T> T first(T[] array) {
        return isEmpty(array) ? null : array[0];
    }

    public static <T> T last(T[] array) {
        return isEmpty(array) ? null : array[array.length - 1];
    }


    public static <T> void reverse(T[] array) {
        if (isEmpty(array)) {
            return;
        }

        int left = 0;
        int right = array.length - 1;

        while (left < right) {
            T temp = array[left];
            array[left] = array[right];
            array[right] = temp;

            left++;
            right--;
        }
    }

    /**
     * 交换数组中两个位置的元素。
     *
     * @param array 数组
     * @param i     第一个元素下标
     * @param j     第二个元素下标
     * @param <T>   元素类型
     */
    public static <T> void swap(T[] array, int i, int j) {
        T temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}