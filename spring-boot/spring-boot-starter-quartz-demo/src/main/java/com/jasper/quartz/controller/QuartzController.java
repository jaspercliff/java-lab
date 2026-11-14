package com.jasper.quartz.controller;

import com.jasper.quartz.config.QuartzDemoProperties;
import com.jasper.quartz.dto.CronPreview;
import com.jasper.quartz.dto.CronUpdateRequest;
import com.jasper.quartz.dto.ExecutionRecord;
import com.jasper.quartz.dto.ExecutingJobView;
import com.jasper.quartz.dto.JobRequest;
import com.jasper.quartz.dto.JobView;
import com.jasper.quartz.dto.SchedulerInfo;
import com.jasper.quartz.listener.ExecutionHistory;
import com.jasper.quartz.service.QuartzCalendarService;
import com.jasper.quartz.service.QuartzJobService;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Quartz 管理端 REST API。
 *
 * <p>这些接口就是“动态调度”的典型实现：任务定义不写在代码里，而是由管理端写入 Scheduler。
 * 生产环境应加鉴权/审计，并限制可加载的 Job 类（本 Demo 为方便学习允许任意实现 Job 的类）。
 *
 * <p>常用 curl：
 * <pre>{@code
 * # 查看调度器
 * curl localhost:8090/api/quartz/scheduler
 *
 * # 查看所有任务
 * curl localhost:8090/api/quartz/jobs
 *
 * # 创建一个每 10 秒执行的任务
 * curl -X POST localhost:8090/api/quartz/jobs \
 *   -H 'Content-Type: application/json' \
 *   -d '{"name":"myJob","jobClass":"com.jasper.quartz.job.HelloWorldJob",
 *        "triggerType":"SIMPLE","intervalSeconds":10,
 *        "data":{"message":"hello"}}'
 *
 * # 改成每天 9 点
 * curl -X PUT localhost:8090/api/quartz/jobs/demo/myJob/cron \
 *   -H 'Content-Type: application/json' \
 *   -d '{"cron":"0 0 9 * * ?","timeZone":"Asia/Shanghai"}'
 *
 * # 立即执行
 * curl -X POST localhost:8090/api/quartz/jobs/demo/myJob/trigger \
 *   -H 'Content-Type: application/json' -d '{"message":"manual"}'
 *
 * # 暂停 / 恢复 / 删除
 * curl -X POST localhost:8090/api/quartz/jobs/demo/myJob/pause
 * curl -X POST localhost:8090/api/quartz/jobs/demo/myJob/resume
 * curl -X DELETE localhost:8090/api/quartz/jobs/demo/myJob
 * }</pre>
 */
@RestController
@RequestMapping("/api/quartz")
public class QuartzController {

    private final QuartzJobService jobService;
    private final QuartzCalendarService calendarService;
    private final ExecutionHistory executionHistory;
    private final Scheduler scheduler;
    private final QuartzDemoProperties properties;

    public QuartzController(QuartzJobService jobService,
                            QuartzCalendarService calendarService,
                            ExecutionHistory executionHistory,
                            Scheduler scheduler,
                            QuartzDemoProperties properties) {
        this.jobService = jobService;
        this.calendarService = calendarService;
        this.executionHistory = executionHistory;
        this.scheduler = scheduler;
        this.properties = properties;
    }

    // ------------------------------------------------------------------
    // 调度器信息
    // ------------------------------------------------------------------

    @GetMapping("/scheduler")
    public SchedulerInfo schedulerInfo() throws SchedulerException {
        return jobService.schedulerInfo();
    }

    @GetMapping("/scheduler/executing")
    public List<ExecutingJobView> currentlyExecuting() throws SchedulerException {
        return jobService.currentlyExecuting();
    }

    @GetMapping("/history")
    public List<ExecutionRecord> history(@RequestParam(defaultValue = "20") int limit) {
        return executionHistory.recent(limit);
    }

    // ------------------------------------------------------------------
    // Job 查询 / 创建 / 删除
    // ------------------------------------------------------------------

    @GetMapping("/jobs")
    public List<JobView> listJobs() throws SchedulerException {
        return jobService.listJobs();
    }

    @GetMapping("/jobs/{group}/{name}")
    public JobView getJob(@PathVariable String group, @PathVariable String name) throws SchedulerException {
        return jobService.getJob(group, name);
    }

    @PostMapping("/jobs")
    public ResponseEntity<JobView> createJob(@RequestBody JobRequest request) throws SchedulerException {
        JobView view = jobService.createJob(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(view);
    }

    @PutMapping("/jobs/{group}/{name}/cron")
    public JobView rescheduleCron(@PathVariable String group,
                                  @PathVariable String name,
                                  @RequestBody CronUpdateRequest request) throws SchedulerException {
        return jobService.rescheduleCron(group, name, request);
    }

    @DeleteMapping("/jobs/{group}/{name}")
    public ResponseEntity<Void> deleteJob(@PathVariable String group,
                                          @PathVariable String name) throws SchedulerException {
        jobService.deleteJob(group, name);
        return ResponseEntity.noContent().build();
    }

    // ------------------------------------------------------------------
    // Job 控制
    // ------------------------------------------------------------------

    @PostMapping("/jobs/{group}/{name}/pause")
    public ResponseEntity<Void> pauseJob(@PathVariable String group,
                                         @PathVariable String name) throws SchedulerException {
        jobService.pauseJob(group, name);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/jobs/{group}/{name}/resume")
    public ResponseEntity<Void> resumeJob(@PathVariable String group,
                                          @PathVariable String name) throws SchedulerException {
        jobService.resumeJob(group, name);
        return ResponseEntity.noContent().build();
    }

    /**
     * 立即触发一次；请求体可选，作为本次执行的 Trigger 数据。
     */
    @PostMapping("/jobs/{group}/{name}/trigger")
    public Map<String, Object> triggerNow(@PathVariable String group,
                                          @PathVariable String name,
                                          @RequestBody(required = false) Map<String, Object> data)
            throws SchedulerException {
        jobService.triggerNow(group, name, data);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("triggered", true);
        result.put("job", group + "." + name);
        result.put("data", data == null ? Map.of() : data);
        return result;
    }

    @PostMapping("/jobs/{group}/{name}/interrupt")
    public Map<String, Object> interrupt(@PathVariable String group,
                                         @PathVariable String name) throws SchedulerException {
        boolean interrupted = jobService.interrupt(group, name);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("job", group + "." + name);
        result.put("interrupted", interrupted);
        return result;
    }

    // ------------------------------------------------------------------
    // Trigger 控制
    // ------------------------------------------------------------------

    @PostMapping("/triggers/{group}/{name}/pause")
    public ResponseEntity<Void> pauseTrigger(@PathVariable String group,
                                             @PathVariable String name) throws SchedulerException {
        jobService.pauseTrigger(group, name);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/triggers/{group}/{name}/resume")
    public ResponseEntity<Void> resumeTrigger(@PathVariable String group,
                                              @PathVariable String name) throws SchedulerException {
        jobService.resumeTrigger(group, name);
        return ResponseEntity.noContent().build();
    }

    // ------------------------------------------------------------------
    // Cron 预览
    // ------------------------------------------------------------------

    /**
     * 示例：{@code GET /api/quartz/cron/next?expression=0 0 9 * * ?&count=5}
     * （curl 中空格需要 URL 编码）。
     */
    @GetMapping("/cron/next")
    public CronPreview nextFireTimes(@RequestParam String expression,
                                     @RequestParam(required = false) String timeZone,
                                     @RequestParam(defaultValue = "5") int count) {
        return jobService.previewCron(expression, timeZone, count);
    }

    // ------------------------------------------------------------------
    // Calendar
    // ------------------------------------------------------------------

    @GetMapping("/calendars")
    public Map<String, Object> calendars() throws SchedulerException {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("calendarName", properties.holidayCalendar());
        result.put("names", scheduler.getCalendarNames());
        result.put("excludedDates", calendarService.excludedDates());
        return result;
    }

    @PostMapping("/calendars/{name}/holidays")
    public List<Instant> addHoliday(@PathVariable String name,
                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date)
            throws SchedulerException {
        requireHolidayCalendar(name);
        return calendarService.addHoliday(date);
    }

    @DeleteMapping("/calendars/{name}/holidays")
    public List<Instant> removeHoliday(@PathVariable String name,
                                       @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date)
            throws SchedulerException {
        requireHolidayCalendar(name);
        return calendarService.removeHoliday(date);
    }

    // ------------------------------------------------------------------
    // Misfire 演示
    // ------------------------------------------------------------------

    /**
     * 创建一个“暂停中的一次性 Trigger”，用于演示 misfire。
     *
     * <p>调用后等待超过 5 秒（application.yml 中的 misfireThreshold），再调用：
     * {@code POST /api/quartz/triggers/demo/misfireJob-trigger/resume}
     */
    @PostMapping("/demo/misfire")
    public JobView misfireDemo(@RequestParam(defaultValue = "5") int delaySeconds) throws SchedulerException {
        return jobService.scheduleMisfireDemo(delaySeconds);
    }

    private void requireHolidayCalendar(String name) {
        if (!properties.holidayCalendar().equals(name)) {
            throw new IllegalArgumentException("只支持操作日历: " + properties.holidayCalendar());
        }
    }
}
