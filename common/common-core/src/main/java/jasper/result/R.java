package jasper.result;

/**
 * 通用返回
 * @param code
 * @param message
 * @param data
 * @param <T>
 */
public record R<T>(int code, String message, T data) {
    public static <T> R<T> success(T data) {
        return new R<>(200, "success", data);
    }

    public static <T> R<T> error(int code, String message) {
        return new R<>(code, message, null);
    }

    public static <T> R<T> error(int code, String message, T data) {
        return new R<>(code, message, data);
    }
}
