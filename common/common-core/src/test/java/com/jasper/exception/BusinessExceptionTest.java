package com.jasper.exception;

import com.jasper.result.IBusinessResultCode;
import com.jasper.result.ResultCode;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BusinessExceptionTest {

    @Test
    void legacyStringCodeFallsBackToBadRequest() {
        BusinessException ex = new BusinessException("ORDER_001", "库存不足");

        assertThat(ex.getCode()).isEqualTo("ORDER_001");
        assertThat(ex.hasResultCode()).isFalse();
        assertThat(ex.getNumericCode()).isEqualTo(ResultCode.BAD_REQUEST.getCode());
    }

    @Test
    void resultCodeDrivesNumericCode() {
        BusinessException ex = new BusinessException(ResultCode.CONFLICT, "冲突了");

        assertThat(ex.hasResultCode()).isTrue();
        assertThat(ex.getNumericCode()).isEqualTo(50000);
        assertThat(ex.getCode()).isEqualTo("50000");
        assertThat(ex.getMessage()).isEqualTo("冲突了");
    }

    @Test
    void defaultMessageComesFromResultCode() {
        BusinessException ex = new BusinessException(ResultCode.FORBIDDEN);

        assertThat(ex.getMessage()).isEqualTo(ResultCode.FORBIDDEN.getMsg());
        assertThat(ex.getNumericCode()).isEqualTo(403);
        assertThat(ex.getI18nKey()).isEqualTo("SYS403");
    }

    @Test
    void businessCodeDrivesStringCodeAndI18nKey() {
        BusinessException ex = new BusinessException(StockCode.STOCK_NOT_ENOUGH);

        assertThat(ex.getCode()).isEqualTo("RB1001");
        assertThat(ex.getI18nKey()).isEqualTo("RB1001");
        assertThat(ex.getMessage()).isEqualTo("库存不足");
        assertThat(ex.hasBusinessResultCode()).isTrue();
        assertThat(ex.hasResultCode()).isFalse();
    }

    private enum StockCode implements IBusinessResultCode {
        STOCK_NOT_ENOUGH("RB1001", "库存不足");

        private final String code;
        private final String desc;

        StockCode(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        @Override
        public String getCode() {
            return code;
        }

        @Override
        public String getDesc() {
            return desc;
        }
    }
}
