package com.jasper.quartz.dto;

import java.time.Instant;
import java.util.List;

/**
 * 调度器运行时信息，来源 {@code Scheduler#getMetaData()}。
 *
 * <p>排查生产问题时优先看：
 * <ul>
 *   <li>{@code jobStoreClass}：RAMJobStore 还是 LocalDataSourceJobStore（JDBC）</li>
 *   <li>{@code jobStoreClustered}：是否开启集群</li>
 *   <li>{@code threadPoolSize}：线程池大小，长任务多时要调大</li>
 *   <li>{@code numberOfJobsExecuted}：本实例启动以来执行次数</li>
 *   <li>{@code standby}：是否处于待机（不触发但线程池还活着）</li>
 * </ul>
 */
public record SchedulerInfo(
        String schedulerName,
        String instanceId,
        String schedulerClass,
        String jobStoreClass,
        boolean jobStoreClustered,
        boolean jobStoreSupportsPersistence,
        String threadPoolClass,
        int threadPoolSize,
        int numberOfJobsExecuted,
        Instant runningSince,
        boolean started,
        boolean standby,
        boolean shutdown,
        String version,
        List<String> calendarNames,
        List<String> jobGroups,
        List<String> triggerGroups
) {
}
