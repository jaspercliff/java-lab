package jasper.lang;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MapUtil {
    /**
     * 将 List<T> 转换为 Map<K, T>
     * @param list 待转换的 List
     * @param keyExtractor 键提取函数（例如 T::getId）
     * @return Map<K, T>
     */
    public static <K, T> Map<K, T> listToMap(List<T> list, Function<? super T, ? extends K> keyExtractor) {
        return list.stream()
            .collect(Collectors.toMap(
                keyExtractor,
                Function.identity(),
                (v1, v2) -> v1 // 如果 key 冲突，保留第一个
            ));
    }
}