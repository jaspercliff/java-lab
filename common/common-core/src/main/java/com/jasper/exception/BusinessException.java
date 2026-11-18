package com.jasper.exception;

import com.jasper.result.IBusinessResultCode;
import com.jasper.result.IResultCode;
import com.jasper.result.ResultCode;
import lombok.Getter;

/**
 * 业务异常，支持三类错误码来源：
 * <ul>
 *     <li>业务码枚举 {@link IBusinessResultCode}：code 为 RB1001 形式，直接作为 ApiResult.code；</li>
 *     <li>系统码枚举 {@link IResultCode}：code 为数字，作为 ApiResult.code；</li>
 *     <li>传统 String code（如 ORDER_001）：兼容原有调用方式，ApiResult.code 直接取该字符串。</li>
 * </ul>
 *
 * @author jasper
 */
@Getter
public class BusinessException extends RuntimeException {

    /** 响应码：业务码 RB1001 / 系统码 400 / 传统字符串码 ORDER_001 */
    private final String code;

    /** 系统码枚举，使用系统码构造时有值 */
    private final IResultCode resultCode;

    /** 业务码枚举，使用业务码构造时有值 */
    private final IBusinessResultCode businessResultCode;

    /** 国际化 key：业务码默认与 code 相同，系统码为 SYS + 数字码 */
    private final String i18nKey;

    public BusinessException(String code, String message) {
        this(code, message, null, null, null, code);
    }

    public BusinessException(String message) {
        this("BUSINESS_ERROR", message);
    }

    public BusinessException(String code, String message, Throwable cause) {
        this(code, message, cause, null, null, code);
    }

    /** 用系统错误码枚举构造。 */
    public BusinessException(IResultCode resultCode) {
        this(resultCode, resultCode.getMsg(), null);
    }

    public BusinessException(IResultCode resultCode, String message) {
        this(resultCode, message, null);
    }

    public BusinessException(IResultCode resultCode, String message, Throwable cause) {
        super(message, cause);
        this.resultCode = resultCode;
        this.businessResultCode = null;
        this.code = String.valueOf(resultCode.getCode());
        this.i18nKey = resultCode.getI18nKey();
    }

    /** 用业务错误码枚举构造，message 默认取枚举的描述。 */
    public BusinessException(IBusinessResultCode businessCode) {
        this(businessCode, businessCode.getDesc(), null);
    }

    public BusinessException(IBusinessResultCode businessCode, String message) {
        this(businessCode, message, null);
    }

    public BusinessException(IBusinessResultCode businessCode, String message, Throwable cause) {
        super(message, cause);
        this.businessResultCode = businessCode;
        this.resultCode = null;
        this.code = businessCode.getCode();
        this.i18nKey = businessCode.getMsg();
    }

    private BusinessException(String code, String message, Throwable cause,
                              IResultCode resultCode, IBusinessResultCode businessResultCode, String i18nKey) {
        super(message, cause);
        this.code = code;
        this.resultCode = resultCode;
        this.businessResultCode = businessResultCode;
        this.i18nKey = i18nKey;
    }

    public boolean hasResultCode() {
        return resultCode != null;
    }

    public boolean hasBusinessResultCode() {
        return businessResultCode != null;
    }

    /** 数值错误码：系统码枚举构造时取枚举 code，否则回落 BAD_REQUEST。 */
    public int getNumericCode() {
        return hasResultCode() ? resultCode.getCode() : ResultCode.BAD_REQUEST.getCode();
    }
}
