package com.jasper.quartz.job;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

/**
 * 演示两个极常用、也极易被误解的注解：
 *
 * <ul>
 *   <li>{@link DisallowConcurrentExecution}：同一个 JobKey 同一时刻只允许一个实例执行。
 *       注意它约束的是 JobDetail（Key），不是 Job 类；不同 group 的同名 Job 仍可并发。</li>
 *   <li>{@link org.quartz.PersistJobDataAfterExecution}：本次执行结束后，把 JobDetail 的
 *       JobDataMap 写回 JobStore。没有它，你在 execute 里改的 JobDataMap 只在本次执行内有效。</li>
 * </ul>
 *
 * <p>为什么这两个注解通常一起用？
 * “读 count → 加 1 → 写回”不是原子操作。如果允许并发，两个线程可能都读到 1，最后都写 2。
 * 加上 {@code @DisallowConcurrentExecution} 后，同一 JobKey 串行执行，计数才安全。
 *
 * <p>注意：这两个注解对 RAMJobStore 和 JDBCJobStore 都生效；但“持久化”只对 JDBC 有意义
 * （RAM 重启即丢）。
 */
@Slf4j
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class CounterJob implements Job {

    @Override
    public void execute(JobExecutionContext context) {
        // JobDetail 上的 JobDataMap 是可变的；本类执行完会被框架写回。
        JobDataMap data = context.getJobDetail().getJobDataMap();

        int current = Integer.parseInt(data.getString("count"));
        int next = current + 1;

        data.put("count", String.valueOf(next));
        // JDBC 模式下值必须可序列化；Long 是安全的。
        data.put("lastFireTime", String.valueOf(System.currentTimeMillis()));

        log.info("[CounterJob] job={} 第 {} 次执行，累计数据已写回 JobDataMap（lastFireTime={}）",
                context.getJobDetail().getKey(), next, data.getLongValue("lastFireTime"));

        // 与 JobListener 配合，可在审计表里看到每次执行的返回值
        context.setResult(next);
    }
}
