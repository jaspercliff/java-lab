package jasper.juc.executor;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author jasper
 * @since 2026-09-09 17:01:11 <br>
 */
public class CustomRejectedExecutionHandler implements RejectedExecutionHandler {

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {

        // 业务降级或补偿
        //  将任务持久化到数据库/Redis/MQ，后续通过定时任务重试
        // 再多则使用mq
    }
}
