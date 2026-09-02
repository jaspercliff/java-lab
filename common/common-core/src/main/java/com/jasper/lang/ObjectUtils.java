package com.jasper.lang;

import com.jasper.result.FieldDifference;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 顶级对象与空值处理增强工具类（Language Utilities） 。
 *
 * @author Jasper
 * @since 1.0.0
 */
public final class ObjectUtils {

    private ObjectUtils() {} // 防实例化

    /** 判断对象是否为空（支持 String、Collection、Map 以及普通 Object） */
    public static boolean isNull(Object o) {
        return isEmptyObject(o);
    }

    /** 判断对象是否不为空 */
    public static boolean isNotNull(Object o) {
        return !isEmptyObject(o);
    }

    /** 【修复版】判断是否**全部**为空 */
    public static boolean isNullAll(Object... objects) {
        if (objects == null) {
            return true;
        }
        for (Object obj : objects) {
            if (!isEmptyObject(obj)) {
                return false;
            }
        }
        return true;
    }

    /** 【修复版】判断是否**全部**都不为空（严格模式） */
    public static boolean isNotNullAll(Object... objects) {
        if (objects == null || objects.length == 0) {
            return false;
        }
        for (Object obj : objects) {
            if (isEmptyObject(obj)) {
                return false; // 只要有一个为空，就不满足“全部不为空”
            }
        }
        return true;
    }

    /** 【修复版】利用现代 Java 模式匹配，完美兼容 Map 与 Collection 平级关系 */
    private static boolean isEmptyObject(Object o) {
        return switch (o) {
            case null -> true;
            case String str -> str.isEmpty();
            case Map<?, ?> map -> map.isEmpty(); // 🔥 提出来与 Collection 平级，Bug 修复！
            case Collection<?> col -> col.isEmpty(); // 简化：List/Set 都是 Collection，直接调用 isEmpty() 即可
            default -> false;
        };
    }

    /** 检查目标字符串是否在指定的数组/可变参数中 */
    public static boolean isIn(String source, String... targets) {
        if (source == null || targets == null) {
            return false;
        }
        for (String target : targets) {
            if (source.equals(target)) {
                return true;
            }
        }
        return false;
    }

    /** 安全的对象等值比较（规避 NPE） */
    public static boolean isEquals(Object o1, Object o2) {
        return Objects.equals(o1, o2); // 直接借力 Java 原生最稳健的比较
    }

    public static boolean isNotEquals(Object o1, Object o2) {
        return !isEquals(o1, o2);
    }

    /**
     * 比较两个对象的字段值，返回值不同的字段名及其对应的旧值和新值。 一些变更记录 字段的变更 变更操作历史
     *
     * @param oldObj 旧的对象实例
     * @param newObj 新的对象实例
     * @return 值不同的字段列表，包含字段名、旧值和新值
     * @throws IllegalAccessException 如果无法访问字段
     */
    public static List<FieldDifference> compare(Object oldObj, Object newObj)
            throws IllegalAccessException {
        if (oldObj == null || newObj == null) {
            throw new IllegalArgumentException("Both objects must not be null");
        }

        List<FieldDifference> differences = new ArrayList<>();
        Field[] fields = oldObj.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);
            Object oldValue = field.get(oldObj);
            Object newValue = field.get(newObj);

            if (isNotNull(oldValue)) {
                if (!oldValue.equals(newValue)) {
                    differences.add(new FieldDifference(null, field.getName(), oldValue, newValue));
                }
            }
            if (isNull(oldValue) && isNotNull(newValue)) {
                differences.add(new FieldDifference(null, field.getName(), oldValue, newValue));
            }
        }

        return differences;
    }
}
