package jasper.result;

/**
 * 响应码接口，方便自定义业务状态码枚举
 */
public interface IResultCode {
    /**
     * 获取状态码
     */
    Integer getCode();

    /**
     * 获取提示消息
     */
    String getMsg();
}