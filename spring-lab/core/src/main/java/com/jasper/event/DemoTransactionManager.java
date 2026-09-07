package com.jasper.event;

import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;

public class DemoTransactionManager
        extends AbstractPlatformTransactionManager {

    @Override
    protected Object doGetTransaction() {
        return new Object();
    }

    @Override
    protected void doBegin(
            Object transaction,
            org.springframework.transaction.TransactionDefinition definition
    ) {
        System.out.println("开启事务");
    }

    @Override
    protected void doCommit(DefaultTransactionStatus status) {
        System.out.println("提交事务");
    }

    @Override
    protected void doRollback(DefaultTransactionStatus status) {
        System.out.println("回滚事务");
    }
}