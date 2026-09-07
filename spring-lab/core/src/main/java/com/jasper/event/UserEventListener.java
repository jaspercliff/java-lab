package com.jasper.event;

import lombok.extern.slf4j.Slf4j;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 如果监听器没有加 @Async，它是同步执行的。如果监听器抛出异常，会导致发布事件的主方法也抛出异常（甚至导致主事务回滚）。<br>
 * 如果希望监听器的异常不影响主流程，必须在监听器内部 try-catch 捕获异常，或者使用异步
 */
@Slf4j
@Component
public class UserEventListener {

    // 监听用户注册事件，发送邮件
    @EventListener
    public void handleUserRegister(UserRegisterEvent event) {
        log.info("send email to {}", event.email());
    }

    // 监听用户注册事件，赠送新人积分
    @EventListener
    public void handleUserRegisterBonus(UserRegisterEvent event) {
        log.info("gift point to {}", event.username());
    }

    @Async // 该方法将在异步线程池中执行
    @EventListener(condition = "#root.event.payload.username() == 'admin'")
    public void handleAsyncEvent(UserRegisterEvent event) {
        System.out.println("admin 异步处理: " + Thread.currentThread().getName());
    }

    /**
     * 痛点：默认事件是同步的，如果在事件监听器中发生异常导致事务回滚，但事件监听器可能已经执行了部分操作
     * （或者在事务提交前就去读取数据库，导致读不到刚插入的数据）。
     * 解决方案：使用 @TransactionalEventListener，它可以控制事件在事务的特定阶段触发（如事务提交后、回滚后）
     */
    // 事务提交后执行
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleAfterCommit(UserRegisterEvent event) {
        log.info(
                "事务提交后：发送邮件给 {}，线程：{}",
                event.email(),
                Thread.currentThread().getName()
        );
    }

    // 事务回滚后执行
    @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
    public void handleAfterRollback(UserRegisterEvent event) {
        log.info(
                "事务回滚后：记录失败日志，用户：{}",
                event.username()
        );
    }

    // 事务完成后执行，无论提交还是回滚
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMPLETION)
    public void handleAfterCompletion(UserRegisterEvent event) {
        log.info(
                "事务完成：{}",
                event.username()
        );
    }
}
