# quartz-demo

---

## 1. 快速开始

### 1.1 默认模式：RAMJobStore（零依赖）

```bash
# 在仓库根目录
./gradlew :quartz-demo:bootRun
```

> 如果 `application.yml` 里启用了 `spring.profiles.active=jdbc`，这条命令会直接连 MySQL，
> 不再走 RAMJobStore；想回到零依赖模式，注释掉 `profiles.active` 即可。
> 单元测试使用 `@ActiveProfiles("test")`，不会依赖外部数据库。

启动后：

- 服务端口：`http://localhost:8090`
- 调度器信息：`GET http://localhost:8090/api/quartz/scheduler`
- 任务列表：`GET http://localhost:8090/api/quartz/jobs`

默认会注册 7 个演示任务（`demo` 组）和 1 个声明式任务（`declarative` 组）：

| 任务 | 说明 |
| --- | --- |
| `helloJob` | 每 10 秒，演示 JobDataMap、Trigger 数据覆盖、fireTime |
| `counterJob` | 每 15 秒，`@DisallowConcurrentExecution` + `@PersistJobDataAfterExecution` |
| `slowJob` | 每 60 秒、执行约 20 秒，演示 `Scheduler#interrupt` |
| `failureJob` | 每 30 秒，前 2 次失败并立即重试，第 3 次成功 |
| `springBeanJob` | 每 20 秒，演示 Job 中注入 Spring Bean |
| `durableJob` | 无 Trigger，只能手动触发 |
| `calendarJob` | 每天 09:00，节假日由 `cnHolidays` 排除 |
| `declarativeJob` | 每 2 分钟，由 Spring `@Bean` 声明式注册 |

RAMJobStore 的特点是**重启即丢**，适合学习和本地调试。

### 1.2 JDBC 模式：持久化 + 集群

仓库根目录已提供 MySQL 8.4 的 compose 配置（映射到宿主机 `3307`）：

```bash
docker compose up -d db

./gradlew :quartz-demo:bootRun --args='--spring.profiles.active=jdbc'
```

连接信息在 `src/main/resources`：

- URL：`jdbc:mysql://127.0.0.1:3307/java-lab`
- 用户：`jasper` / `passwd`（来自仓库根目录 `.env`）

表结构：

```text
src/main/resources/db/quartz-mysql.sql
```

该脚本是幂等的（`CREATE TABLE IF NOT EXISTS`），`initialize-schema=always` 不会清空已有任务。
生产环境建议改成 `never`，用 Flyway/Liquibase 管理。

### 1.3 跑测试

```bash
./gradlew :quartz-demo:test
```

包含：

- `CronExpressionTest`：Cron 解析、工作日跳过周末、非法表达式；
- `QuartzStandaloneTest`：不启动 Spring 的最小 Quartz 调度；
- `QuartzDemoApplicationTests`：Spring 上下文、自动配置、Cron 预览。

---

## 2. 知识点地图

| 知识点 | 代码位置 | 关键说明 |
| --- | --- | --- |
| `Job` / `JobDetail` / `JobKey` | `job/HelloWorldJob.java` | Job 无状态、每次 new；JobDetail 持久化“做什么 + 用什么数据” |
| `JobDataMap` | `job/HelloWorldJob.java`、`job/CounterJob.java` | getMergedJobDataMap() 合并 JobDetail 与 Trigger jobDataMap，同名 Trigger 设置的data优先 |
| `JobExecutionContext` | `job/HelloWorldJob.java` | fireTime / scheduledFireTime / nextFireTime / refireCount / result |
| `@DisallowConcurrentExecution` | `job/CounterJob.java` | 约束的是 **JobKey**，不是 Job 类；不同 group 的同名 Job 仍可并发 |
| `@PersistJobDataAfterExecution` | `job/CounterJob.java` | 执行后把 JobDataMap 写回 JobStore；通常与非并发注解一起用 |
| Counter 累积值 / 原始 BLOB 反序列化 | `controller/QuartzDebugController.java` | `GET /debug/counter` 走 Scheduler API；`/debug/counter/raw` 直接读 `QRTZ_JOB_DETAILS.JOB_DATA` |
| `InterruptableJob` | `job/SlowJob.java` | `Scheduler#interrupt` 只对实现该接口的 Job 生效 |
| `JobExecutionException` | `job/FailureJob.java` | `setRefireImmediately` / `setUnscheduleAllTriggers` / `setUnscheduleFiringTrigger` |
| Spring 注入 Job | `job/SpringBeanJob.java`、`config/TracingJobFactory.java` | `SpringBeanJobFactory` 通过 `AutowireCapableBeanFactory` 创建 Job |
| Durable Job | `job/DurableJob.java`、`bootstrap/QuartzBootstrap.java` | 无 Trigger 也不删除，适合手动触发 |
| SimpleTrigger | `QuartzJobService#buildTrigger` | 固定间隔/次数；`repeatCount=-1` 永久；startAt/endAt |
| CronTrigger | `QuartzJobService#buildTrigger` | 日历语义；时区、`L`/`W`/`#`；生产最常用 |
| CalendarIntervalTrigger | `QuartzJobService#buildTrigger` | 按自然月/年递增，正确处理 1 月 31 日 + 1 个月 |
| DailyTimeIntervalTrigger | `QuartzJobService#buildTrigger` | 每天时间窗口内按间隔执行，例如工作日 9-18 点 |
| Trigger 优先级 | `TriggerBuilder#withPriority` | 线程不够时优先级高的先执行；不是“抢占” |
| Trigger 状态 | `TriggerView#state` | NORMAL / PAUSED / COMPLETE / ERROR / BLOCKED / NONE |
| `JobListener` | `listener/GlobalJobListener.java` | 执行前、被否决、执行后；记录耗时与结果 |
| `TriggerListener` | `listener/GlobalTriggerListener.java` | 触发、veto、misfire、完成；veto 可阻止本次执行 |
| `SchedulerListener` | `listener/GlobalSchedulerListener.java` | 调度器生命周期与 Job/Trigger 元数据变更 |
| Listener Matcher | `bootstrap/QuartzBootstrap.java`、`listener/GroupJobListener.java`、`listener/SpecificTriggerListener.java` | GroupMatcher / KeyMatcher / OrMatcher |
| Calendar 节假日 | `config/QuartzConfig#cnHolidays`、`service/QuartzCalendarService.java` | HolidayCalendar；修改后必须重新 `addCalendar(..., true, true)` |
| Misfire 策略 | `dto/MisfirePolicy.java`、`QuartzJobService#apply*Misfire` | 不同 Trigger 支持的策略不同；Cron 常用 DO_NOTHING |
| Misfire 演示 | `QuartzJobService#scheduleMisfireDemo` | 暂停 → 等待超过阈值 → 恢复，观察 triggerMisfired |
| 动态增删改查 | `service/QuartzJobService.java` | scheduleJob / addJob / rescheduleJob / deleteJob |
| 暂停/恢复 | `QuartzJobService#pauseJob` 等 | Job 与 Trigger 两个维度；`pauseAll/resumeAll` 未暴露 |
| 立即执行 | `QuartzJobService#triggerNow` | `triggerJob(jobKey, data)`，本次数据参与合并 |
| 中断 | `QuartzJobService#interrupt` | 返回是否至少中断了一个执行实例 |
| RAMJobStore / JDBCJobStore | `application.yml`、`application-jdbc.yml` | 内存 vs 持久化；JDBC 支持集群和故障恢复 |
| 集群 | `application-jdbc.yml` | `isClustered`、`instanceId=AUTO`、`clusterCheckinInterval`、`acquireTriggersWithinLock` |
| Spring Boot 自动配置 | `config/QuartzConfig.java` | `SchedulerFactoryBeanCustomizer`、`SpringBeanJobFactory`、`Map<String, Calendar>` |
| 声明式注册 | `config/DeclarativeQuartzConfig.java` | `JobDetail`/`Trigger` 作为 Bean，由 `SchedulerFactoryBean` 自动注册 |
| 优雅停机 | `application.yml`、`GlobalSchedulerListener` | `wait-for-jobs-to-complete-on-shutdown` + `server.shutdown=graceful` |
| Cron 预览 | `QuartzJobService#previewCron` | `CronExpression#getNextValidTimeAfter` |

---

## 5. Cron 预览

```bash
curl 'http://localhost:8090/api/quartz/cron/next?expression=0%200%209%20*%20*%20%3F&count=5'
```

返回：

```json
{
  "expression": "0 0 9 * * ?",
  "timeZone": "Asia/Shanghai",
  "nextFireTimes": [
    "2026-11-12T01:00:00Z",
    "2026-11-13T01:00:00Z"
  ]
}
```

输出是 UTC（`Z`），对应北京时间 09:00。




### 6.3 veto：阻止本次执行

`GlobalTriggerListener#vetoJobExecution` 读取 JobDataMap：

```bash
curl -X POST localhost:8090/api/quartz/jobs \
  -H 'Content-Type: application/json' \
  -d '{"name":"vetoJob","jobClass":"com.jasper.quartz.job.HelloWorldJob",
       "triggerType":"SIMPLE","intervalSeconds":10,
       "data":{"veto":true}}'
```

本次执行会被跳过，`GlobalJobListener#jobExecutionVetoed` 会被调用。
生产中 veto 更适合“集群锁没抢到”“灰度关闭”等框架级场景，不要拿它替代业务判断。

---

## 7. Calendar：节假日/维护窗口

`QuartzConfig` 注册了一个 `HolidayCalendar` Bean，名称 `cnHolidays`：

```java
@Bean(name = "cnHolidays")
public HolidayCalendar cnHolidays() {
    HolidayCalendar calendar = new HolidayCalendar();
    calendar.addExcludedDate(...);
    return calendar;
}
```

Spring Boot 会收集容器里的 `Map<String, Calendar>` 并调用 `SchedulerFactoryBean#setCalendars`。
Trigger 通过名称引用：

```java
TriggerBuilder.newTrigger()
    .modifiedByCalendar("cnHolidays")
    ...
```

运行时维护：

```bash
# 添加 2026-11-12 为节假日，calendarJob 的下一次触发会自动跳过
curl -X POST 'localhost:8090/api/quartz/calendars/cnHolidays/holidays?date=2026-11-12'

# 移除
curl -X DELETE 'localhost:8090/api/quartz/calendars/cnHolidays/holidays?date=2026-11-12'
```

**关键点**：修改 Calendar 后必须重新注册：

```java
scheduler.addCalendar(name, calendar, true, true);
```

第三个参数 `replace=true`，第四个参数 `updateTriggers=true`。
JDBC 模式下 Calendar 会被序列化进 `QRTZ_CALENDARS`，不重新注册就不会生效/会丢。

其他 Calendar：

- `CronCalendar`：用 Cron 描述排除时段；
- `DailyCalendar`：每天一个时间区间，可跨天；
- `WeeklyCalendar` / `MonthlyCalendar` / `AnnualCalendar`：按周/月/年排除。

---

## 8. Misfire：错失触发

### 8.1 什么情况会 misfire

- 应用停机/重启；
- Scheduler 被 `standby()`；
- 线程池被打满，Trigger 到点但没有空闲线程；
- Trigger 被 `pause`，恢复时已过触发时间；
- 系统时间跳跃。

只有超过 `org.quartz.jobStore.misfireThreshold` 才算 misfire。默认 60 秒；
本 Demo 为了演示改成了 **5 秒**（`application.yml`），生产建议改回 60000。

### 8.2 策略差异（重要）

| 策略 | SimpleTrigger | CronTrigger | Calendar/Daily | 含义 |
| --- | --- | --- | --- | --- |
| `SMART` | ✅ | ✅ | ✅ | Quartz 默认智能选择 |
| `IGNORE` | ✅ | ✅ | ✅ | 补跑所有错过次数（慎用） |
| `FIRE_NOW` | ✅ | ✅ | ✅ | 立即补一次 |
| `DO_NOTHING` | ❌ | ✅ | ✅ | 跳过，等下一次 |
| `NEXT_WITH_*` / `NOW_WITH_*` | ✅ | ❌ | ❌ | Simple 专用，控制次数 |

`QuartzJobService` 会按类型翻译，类型不支持时抛明确的 `IllegalArgumentException`。

### 8.3 动手演示

```bash
# 1. 创建一个 2 秒后触发、但立即被暂停的一次性任务
curl -X POST 'localhost:8090/api/quartz/demo/misfire?delaySeconds=2'

# 2. 等 8 秒（超过 misfireThreshold=5s）
sleep 8

# 3. 恢复 Trigger：会先触发 misfire 回调，再补执行一次
curl -X POST 'localhost:8090/api/quartz/triggers/demo/misfireJob-trigger/resume'

# 4. 看执行历史：scheduledFireTime 与 fireTime 的差值就是延迟
curl 'localhost:8090/api/quartz/history?limit=5'
```

日志中会看到：

```text
[TriggerListener] 发生 misfire trigger=demo.misfireJob-trigger ...
[MisfireJob] scheduledFireTime=... fireTime=... 延迟=6xxxms
```

> 演示用 `IGNORE` 保留原计划时间，便于观察延迟；`FIRE_NOW` 会把 nextFireTime 改成“现在”，
> 在 Job 里看到的 scheduledFireTime 也会变成现在。生产按业务语义选择，不要为了“补跑”打满线程池。

---

## 9. 并发、有状态与持久化

### 9.1 并发

默认同一个 JobKey 可以并发执行。需要串行时加：

```java
@DisallowConcurrentExecution
public class CounterJob implements Job { ... }
```

注意：

- 约束的是 JobKey，不是类；
- 只影响同一个 Scheduler 实例；集群下不同节点仍可能同时执行，需要分布式锁/幂等兜底；
- 加注解后，如果前一次执行很久，后续触发会排队/阻塞，必要时用 `@DisallowConcurrentExecution` + 短任务拆分。

### 9.2 状态

```java
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class CounterJob implements Job {
    public void execute(JobExecutionContext context) {
        JobDataMap data = context.getJobDetail().getJobDataMap();
        data.put("count", data.getIntValue("count") + 1);
    }
}
```

- `@PersistJobDataAfterExecution`：执行后把 JobDataMap 写回 JobStore；
- 没有它，修改只在本次执行内有效；
- 与并发注解一起用，避免“读-改-写”丢失更新；
- RAMJobStore 重启即丢，JDBC 才会真正持久化。

### 9.3 JobDataMap 序列化

JDBC 模式下：

- `useProperties=false`（默认）：JobDataMap 以 Java 序列化 BLOB 存储，**值必须实现 `Serializable`**；
- `useProperties=true`：以字符串 Properties 存储，**所有值必须是 String**，可规避反序列化漏洞，但类型受限。

本 Demo 用 String/Integer/Long/Boolean，两种模式都安全。
生产建议：

- 优先 `useProperties=true`，复杂对象序列化成 JSON 字符串；
- 如果必须用 Java 序列化，限制可写入的类，避免反序列化 RCE；
- 大对象不要塞 JobDataMap（MySQL BLOB 默认 64KB），放对象存储/数据库，JobDataMap 只存 ID。

---

## 10. RAMJobStore vs JDBCJobStore

| 维度 | RAMJobStore | JDBCJobStore |
| --- | --- | --- |
| 持久化 | 否，重启即丢 | 是 |
| 集群 | 不支持 | 支持 |
| 故障恢复 | 不支持 | 支持 `requestRecovery` |
| 依赖 | 无 | DataSource + 事务管理器 + 表 |
| 性能 | 高 | 受数据库影响 |
| 适用 | 本地开发、临时任务 | 生产、需要可靠调度 |

Spring Boot 配置：

```yaml
spring:
  quartz:
    job-store-type: memory   # 或 jdbc
```

JDBC 模式关键项：

```yaml
org.quartz.jobStore.class: org.springframework.scheduling.quartz.LocalDataSourceJobStore
org.quartz.jobStore.driverDelegateClass: org.quartz.impl.jdbcjobstore.StdJDBCDelegate
org.quartz.jobStore.tablePrefix: QRTZ_
org.quartz.jobStore.isClustered: true
org.quartz.jobStore.clusterCheckinInterval: 15000
org.quartz.jobStore.acquireTriggersWithinLock: true
org.quartz.scheduler.instanceId: AUTO
```

### 集群要点

1. 所有节点连**同一个库**，`isClustered=true`，`instanceId=AUTO`；
2. 节点机器时钟要同步（NTP），否则 nextFireTime 会漂移；
3. `QRTZ_LOCKS` 是集群锁表，不要手工清；
4. `QRTZ_SCHEDULER_STATE` 记录节点心跳，长时间不 checkin 的节点会被认为失效；
5. 集群只保证“同一个 Trigger 不会被两个节点同时抢到”，**不保证业务幂等**；
6. 数据库连接池要足够：集群节点数 × 连接数 < MySQL `max_connections`；
7. 高并发获取 Trigger 可调 `batchTriggerAcquisitionMaxCount`，但不要盲目调大；
8. 故障恢复依赖 `requestRecovery(true)` + JDBC，且 Job 必须可重入/幂等。

### 表结构说明

`quartz-mysql.sql` 包含 11 张表：

- `QRTZ_JOB_DETAILS`：Job 定义与 JobDataMap；
- `QRTZ_TRIGGERS`：Trigger 主表；
- `QRTZ_SIMPLE_TRIGGERS` / `QRTZ_CRON_TRIGGERS` / `QRTZ_SIMPROP_TRIGGERS` / `QRTZ_BLOB_TRIGGERS`：各类型 Trigger 的扩展表；
- `QRTZ_CALENDARS`：Calendar 序列化数据；
- `QRTZ_PAUSED_TRIGGER_GRPS`：被暂停的 Trigger 组；
- `QRTZ_FIRED_TRIGGERS`：正在执行的 Trigger（集群排障重点）；
- `QRTZ_SCHEDULER_STATE`：节点心跳；
- `QRTZ_LOCKS`：集群锁。

---

## 11. Spring Boot 集成原理

### 11.1 自动配置做了什么

`QuartzAutoConfiguration`：

1. 创建 `SchedulerFactoryBean`，并设置 `SpringBeanJobFactory`；
2. 读取 `spring.quartz.*`；
3. `job-store-type=jdbc` 时注入 DataSource 和事务管理器；
4. 收集容器里的 `JobDetail`、`Trigger`、`Calendar` Bean 并注册；
5. 通过 `SchedulerFactoryBeanCustomizer` 允许定制。

### 11.2 为什么 Job 里能 @Autowired

`SpringBeanJobFactory#createJobInstance`：

```java
applicationContext.getAutowireCapableBeanFactory().createBean(jobClass)
```

所以支持构造器注入、字段注入、`@Value`；还会把 JobDataMap/Trigger 数据按属性名写入 Job 的 setter。

本 Demo 的 `TracingJobFactory` 继承它并重写 `createJobInstance` 加日志。
**替换 JobFactory 后必须 `setApplicationContext`**，否则注入失效。

### 11.3 声明式 vs 编程式

- 声明式：`DeclarativeQuartzConfig` 把 `JobDetail`/`Trigger` 定义成 Bean，适合固定任务；
- 编程式：`QuartzJobService` 调 Scheduler API，适合后台动态配置；
- 混用时注意 JobKey 不冲突；JDBC 重启时可用 `spring.quartz.overwrite-existing-jobs=true` 覆盖。

---

## 12. REST API 一览

Base URL：`http://localhost:8090/api/quartz`

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/scheduler` | 调度器元数据 |
| GET | `/scheduler/executing` | 当前正在执行的任务 |
| GET | `/history?limit=20` | 最近执行历史（内存） |
| GET | `/jobs` | 所有 Job |
| GET | `/jobs/{group}/{name}` | 单个 Job + Trigger |
| POST | `/jobs` | 创建并调度 |
| PUT | `/jobs/{group}/{name}/cron` | 修改 Cron |
| DELETE | `/jobs/{group}/{name}` | 删除 Job 及 Trigger |
| POST | `/jobs/{group}/{name}/pause` | 暂停 Job |
| POST | `/jobs/{group}/{name}/resume` | 恢复 Job |
| POST | `/jobs/{group}/{name}/trigger` | 立即执行（可带本次数据） |
| POST | `/jobs/{group}/{name}/interrupt` | 中断执行 |
| POST | `/triggers/{group}/{name}/pause` | 暂停 Trigger |
| POST | `/triggers/{group}/{name}/resume` | 恢复 Trigger |
| GET | `/cron/next` | Cron 下次触发预览 |
| GET | `/calendars` | Calendar 列表与排除日期 |
| POST | `/calendars/{name}/holidays?date=yyyy-MM-dd` | 添加节假日 |
| DELETE | `/calendars/{name}/holidays?date=yyyy-MM-dd` | 移除节假日 |
| POST | `/demo/misfire?delaySeconds=5` | 创建 misfire 演示任务 |
| GET | `/debug/counter` | 通过 Scheduler 读取 counterJob 的累积值（RAM/JDBC 都可用） |
| GET | `/debug/counter/raw` | 直接反序列化 `QRTZ_JOB_DETAILS.JOB_DATA`（仅 JDBC） |

### 查看 counter 累积值

`CounterJob` 每次执行会把 `count + 1` 写回 JobDataMap，并靠 `@PersistJobDataAfterExecution` 持久化。
最直接的查看方式：

```bash
curl localhost:8090/api/quartz/jobs/demo/counterJob
# 或只看调试视图
curl localhost:8090/api/quartz/debug/counter
```

返回中的 `jobData.count` 就是累积值：

```json
{
  "source": "Scheduler#getJobDetail",
  "jobStore": "org.springframework.scheduling.quartz.LocalDataSourceJobStore",
  "job": "demo.counterJob",
  "count": 7,
  "lastFireTime": 1794407000000,
  "jobData": {
    "count": 7,
    "lastFireTime": 1794407000000
  }
}
```

如果你想看**数据库里到底存了什么**，用 JDBC 模式启动后调用：

```bash
curl localhost:8090/api/quartz/debug/counter/raw
```

它会把 `QRTZ_JOB_DETAILS.JOB_DATA` 的 BLOB 反序列化：

```json
{
  "source": "QRTZ_JOB_DETAILS.JOB_DATA",
  "jobStore": "org.springframework.scheduling.quartz.LocalDataSourceJobStore",
  "bytes": 123,
  "serializedType": "org.quartz.JobDataMap",
  "count": 7,
  "jobData": {
    "count": 7,
    "lastFireTime": 1794407000000
  }
}
```

实现见 [QuartzDebugController.java](src/main/java/com/jasper/quartz/controller/QuartzDebugController.java)，核心代码：

```java
try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes))) {
    Object object = in.readObject();          // 默认是 org.quartz.JobDataMap
    JobDataMap map = (JobDataMap) object;
    int count = map.getIntValue("count");
}
```

两个注意点：

1. 只有 `org.quartz.jobStore.useProperties=false` 时 `JOB_DATA` 才是 Java 序列化；
   如果设为 `true`，Quartz 存的是 `Properties` 文本，要用 `Properties.load(...)` 解析。
2. 反序列化不可信数据有 RCE 风险。这个调试接口只适合本地学习/排障，生产不要暴露。

### 创建任务示例

每 10 秒执行：

```bash
curl -X POST localhost:8090/api/quartz/jobs \
  -H 'Content-Type: application/json' \
  -d '{
    "name":"myJob",
    "group":"demo",
    "jobClass":"com.jasper.quartz.job.HelloWorldJob",
    "description":"我的第一个动态任务",
    "triggerType":"SIMPLE",
    "intervalSeconds":10,
    "repeatCount":-1,
    "data":{"message":"hello","jobLevelNumber":1}
  }'
```

每天 9 点执行：

```bash
curl -X POST localhost:8090/api/quartz/jobs \
  -H 'Content-Type: application/json' \
  -d '{
    "name":"dailyReport",
    "group":"demo",
    "jobClass":"com.jasper.quartz.job.HelloWorldJob",
    "triggerType":"CRON",
    "cron":"0 0 9 * * ?",
    "timeZone":"Asia/Shanghai",
    "calendar":"cnHolidays",
    "misfirePolicy":"DO_NOTHING"
  }'
```

只注册 durable Job（手动触发）：

```bash
curl -X POST localhost:8090/api/quartz/jobs \
  -H 'Content-Type: application/json' \
  -d '{
    "name":"manualOnly",
    "group":"demo",
    "jobClass":"com.jasper.quartz.job.DurableJob",
    "triggerType":"NONE",
    "durable":true,
    "data":{"note":"手动补数据"}
  }'

curl -X POST localhost:8090/api/quartz/jobs/demo/manualOnly/trigger
```

修改 Cron：

```bash
curl -X PUT localhost:8090/api/quartz/jobs/demo/dailyReport/cron \
  -H 'Content-Type: application/json' \
  -d '{"cron":"0 30 8 * * ?","timeZone":"Asia/Shanghai","misfirePolicy":"DO_NOTHING"}'
```

> 生产环境必须给这些接口加鉴权、审计和 Job 类白名单。
> 本 Demo 为了演示 `Class.forName` 动态加载，允许任意实现 `Job` 的类，**不要直接上线**。

---

## 13. 生产实践与常见坑

### 13.1 任务设计

1. **Job 要短**：Quartz 线程池是有限资源，长任务会拖垮所有调度。耗时任务应投递到业务线程池/MQ。
2. **必须幂等**：misfire、故障恢复、集群重试都可能导致重复执行；用业务唯一键/状态机/分布式锁兜底。
3. **不要用 Quartz 当消息队列**：它不保证 exactly-once，也不适合高吞吐。
4. **失败重试要有退避**：不要在 Job 里 `while(true)` 重试；用延迟队列或单独的重试 Job。
5. **记录 traceId**：`JobExecutionContext` 里没有链路上下文时，在 Job 开头生成/透传 traceId。
6. **优雅停机**：`wait-for-jobs-to-complete-on-shutdown=true` + `server.shutdown=graceful`，并设置合理超时。

### 13.2 配置

1. 线程池大小按“并发任务数 × 单任务耗时 / 触发间隔”估算，不要拍脑袋；
2. `misfireThreshold` 默认 60 秒，按业务调整；
3. Cron 一定要显式时区，避免服务器时区变化/夏令时；
4. JDBC 集群节点必须 NTP 对时；
5. `QRTZ_` 表不要手工改，排障用 `QRTZ_FIRED_TRIGGERS` / `QRTZ_SCHEDULER_STATE`；
6. `spring.quartz.jdbc.initialize-schema=always` 仅适合 Demo；生产用迁移工具；
7. 关闭 Quartz 的 update check（`skipUpdateCheck=true`），避免启动访问外网。

### 13.3 监控

建议至少采集：

- 任务执行成功/失败次数、耗时（Micrometer Timer）；
- 当前正在执行数、线程池活跃数；
- misfire 次数（`TriggerListener#triggerMisfired`）；
- 下一次触发时间与当前时间差（发现调度停滞）；
- `QRTZ_SCHEDULER_STATE` 节点心跳；
- `QRTZ_FIRED_TRIGGERS` 中长时间未完成的记录（卡死任务）。

本 Demo 的 `ExecutionHistory` 是内存版，生产应落库/接入 Prometheus/ELK。

### 13.4 安全

1. 管理端接口必须鉴权（Spring Security）；
2. `jobClass` 使用白名单，不要直接 `Class.forName` 用户输入；
3. `JobDataMap` 不要反序列化不可信数据；优先 `useProperties=true`；
4. 限制 `/interrupt`、`/delete` 等危险操作的权限并审计。

---

## 14. 和 @Scheduled / XXL-JOB 怎么选

| 方案 | 优点 | 缺点 | 适用 |
| --- | --- | --- | --- |
| `@Scheduled` | 简单、零依赖 | 单机、无持久化、无动态管理、无集群 | 简单单机定时 |
| Quartz | 功能完整、持久化、集群、动态 | 需要表/配置，集群只保证不重复抢 Trigger | 中大型 Java 应用 |
| XXL-JOB | 有调度中心、可视化、分片、告警 | 需部署调度中心，架构更重 | 多应用统一调度 |

Quartz 的定位是“**嵌入式调度库**”，不是“调度平台”。如果只需要“每天跑一次”，`@Scheduled` 足够；
如果需要“后台动态配置 + 持久化 + 集群”，Quartz 很合适；如果需要“统一调度中心 + 分片 + 告警”，考虑 XXL-JOB。

---

## 15. 排障清单

| 现象 | 排查方向 |
| --- | --- |
| 任务没跑 | Trigger 是否 PAUSED？`nextFireTime` 是否为 null？线程池是否打满？Cron 时区是否正确？ |
| 任务重复跑 | 是否多节点都开了 Scheduler？是否 `isClustered=false`？业务是否幂等？ |
| 任务延迟 | 线程池是否被长任务占满？是否发生 misfire？`scheduledFireTime` 与 `fireTime` 差多少？ |
| 重启后任务丢失 | 是否用了 RAMJobStore？JDBC 表是否初始化？`job-store-type` 是否正确？ |
| 集群不生效 | 是否连同一个库？`isClustered=true`？`instanceId` 是否唯一？时钟是否同步？ |
| Job 里注入为 null | 是否替换了 JobFactory 但没 `setApplicationContext`？是否绕过了 Spring 直接 new？ |
| 修改 Calendar 不生效 | 是否重新 `addCalendar(..., true, true)`？ |
| 启动报 `ObjectAlreadyExistsException` | JDBC 下同名 Job 已存在；设 `overwrite-existing-jobs=true` 或改用唯一 Key |
| 启动报 DataSource 驱动错误 | 默认 memory 模式已排除 DataSourceAutoConfiguration；检查 profile 是否误激活 |

---

## 16. 关键源码阅读顺序

建议按这个顺序看：

1. `QuartzDemoApplication` → 模块入口；
2. `job/HelloWorldJob` → 最小 Job；
3. `config/QuartzConfig` → Spring 集成、Listener、Calendar；
4. `bootstrap/QuartzBootstrap` → 如何注册各种任务；
5. `job/CounterJob` → 并发 + 状态持久化；
6. `job/SlowJob` → 中断；
7. `job/FailureJob` → 失败重试；
8. `service/QuartzJobService` → 全部 Scheduler API；
9. `controller/QuartzController` → 动态管理；
10. `application-jdbc.yml` + `db/quartz-mysql.sql` → 持久化/集群。

---

## 17. 免责声明

这是教学 Demo，不是生产脚手架。上生产前至少补齐：

- 管理端鉴权与审计；
- Job 类白名单；
- 任务幂等与失败补偿；
- 监控告警；
- 数据库迁移（Flyway/Liquibase）；
- 集群时钟同步；
- 线程池容量压测。
