package com.jasper.quartz.listener;

import com.jasper.quartz.dto.ExecutionRecord;
import org.springframework.stereotype.Component;

import java.util.Deque;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * 内存版执行历史。
 *
 * <p>Listener 的价值在于“可观测 + 可干预”。本例把每次执行结果放进有界队列，
 * 通过 {@code GET /api/quartz/history} 查看。
 *
 * <p>生产实践：
 * <ul>
 *   <li>把执行结果写入数据库/ClickHouse/ELK，字段至少包含 job、trigger、计划时间、实际时间、耗时、结果、异常；</li>
 *   <li>接入 Micrometer：成功/失败计数器、执行耗时 Timer、当前执行数 Gauge；</li>
 *   <li>失败告警要带 jobKey 和 traceId，避免只告警“有任务失败”却不知道是哪一个。</li>
 * </ul>
 */
@Component
public class ExecutionHistory {

    private static final int MAX_SIZE = 200;

    /** addFirst + pollLast 形成“最新在前”的环形队列。 */
    private final Deque<ExecutionRecord> records = new ConcurrentLinkedDeque<>();

    public void add(ExecutionRecord record) {
        records.addFirst(record);
        // 并发下 size 可能短暂超过 MAX_SIZE，这里只做近似裁剪，避免加锁。
        while (records.size() > MAX_SIZE) {
            records.pollLast();
        }
    }

    /**
     * @param limit 最多返回多少条，防止管理端一次拉取过多
     */
    public List<ExecutionRecord> recent(int limit) {
        int safeLimit = Math.clamp(limit, 0, MAX_SIZE);
        return records.stream().limit(safeLimit).toList();
    }
}
