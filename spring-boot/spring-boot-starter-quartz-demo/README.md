# quartz-demo


## veto：阻止本次执行

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

## Misfire：错失触发


- 应用停机/重启；
- Scheduler 被 `standby()`；
- 线程池被打满，Trigger 到点但没有空闲线程；
- Trigger 被 `pause`，恢复时已过触发时间；
- 系统时间跳跃。

只有超过 `org.quartz.jobStore.misfireThreshold` 才算 misfire。默认 60 秒；
为了演示改成了 **5 秒**（`application.yml`），生产建议改回 60000。

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

### JobDataMap 序列化

JDBC 模式下：

- `useProperties=false`（默认）：JobDataMap 以 Java 序列化 BLOB 存储，**值必须实现 `Serializable`**；
- `useProperties=true`：以字符串 Properties 存储，**所有值必须是 String**，可规避反序列化漏洞，但类型受限。

- 优先 `useProperties=true`，复杂对象序列化成 JSON 字符串；
- 如果必须用 Java 序列化，限制可写入的类，避免反序列化 RCE；
- 大对象不要塞 JobDataMap（MySQL BLOB 默认 64KB），放对象存储/数据库，JobDataMap 只存 ID。

---


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


## 自动配置做了什么

`QuartzAutoConfiguration`：

1. 创建 `SchedulerFactoryBean`，并设置 `SpringBeanJobFactory`；
2. 读取 `spring.quartz.*`；
3. `job-store-type=jdbc` 时注入 DataSource 和事务管理器；
4. 收集容器里的 `JobDetail`、`Trigger`、`Calendar` Bean 并注册；
5. 通过 `SchedulerFactoryBeanCustomizer` 允许定制。

##  为什么 Job 里能 @Autowired

`SpringBeanJobFactory#createJobInstance`：

```java
applicationContext.getAutowireCapableBeanFactory().createBean(jobClass)
```

所以支持构造器注入、字段注入、`@Value`；还会把 JobDataMap/Trigger 数据按属性名写入 Job 的 setter。

Demo 的 `TracingJobFactory` 继承它并重写 `createJobInstance` 加日志。
**替换 JobFactory 后必须 `setApplicationContext`**，否则注入失效。

### 11.3 声明式 vs 编程式

- 声明式：`DeclarativeQuartzConfig` 把 `JobDetail`/`Trigger` 定义成 Bean，适合固定任务；
- 编程式：`QuartzJobService` 调 Scheduler API，适合后台动态配置；
- 混用时注意 JobKey 不冲突；JDBC 重启时可用 `spring.quartz.overwrite-existing-jobs=true` 覆盖。

---

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

##  生产实践与常见坑
### 任务设计

1. **Job 要短**：Quartz 线程池是有限资源，长任务会拖垮所有调度。耗时任务应投递到业务线程池/MQ。
2. **必须幂等**：misfire、故障恢复、集群重试都可能导致重复执行；用业务唯一键/状态机/分布式锁兜底。
3. **不要用 Quartz 当消息队列**：它不保证 exactly-once，也不适合高吞吐。
4. **失败重试要有退避**：不要在 Job 里 `while(true)` 重试；用延迟队列或单独的重试 Job。
5. **记录 traceId**：`JobExecutionContext` 里没有链路上下文时，在 Job 开头生成/透传 traceId。
6. **优雅停机**：`wait-for-jobs-to-complete-on-shutdown=true` + `server.shutdown=graceful`，并设置合理超时。

###  配置

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

###  安全

1. `jobClass` 使用白名单，不要直接 `Class.forName` 用户输入；
2. `JobDataMap` 不要反序列化不可信数据；优先 `useProperties=true`；
3. 限制 `/interrupt`、`/delete` 等危险操作的权限并审计。

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

- 管理端鉴权与审计；
- Job 类白名单；
- 任务幂等与失败补偿；
- 监控告警；
- 数据库迁移（Flyway/Liquibase）；
- 集群时钟同步；
- 线程池容量压测。
