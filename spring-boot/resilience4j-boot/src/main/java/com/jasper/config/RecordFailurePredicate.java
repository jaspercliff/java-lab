package com.jasper.config;


import com.jasper.exception.BusinessException;

import java.util.function.Predicate;

/**
 * 自定义失败记录策略
 * 1. 如果异常是 BusinessException 类型，则不计为失败
 * 2. 如果异常是其他类型，则计为失败
 * 断言会优先于 recordExceptions 和 ignoreExceptions 配置
 * 通常使用一个就好
 */
public class RecordFailurePredicate implements Predicate<Throwable> {
    @Override
    public boolean test(Throwable throwable) {
        return !(throwable instanceof BusinessException);
    }
}