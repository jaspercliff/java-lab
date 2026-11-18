# common 工程说明书

> 独立的通用基础工程，被根工程 `java-lab` 的 Spring Boot 各模块以 Maven 坐标引用。
> 定位：**不依赖 Spring**（security 仅依赖 Jackson），可在任意 Java 项目里单独使用。
> 要求 JDK 21+（用到 switch 模式匹配、虚拟线程）。

---

## 目录

- [一、模块地图](#一模块地图)
- [二、快速开始](#二快速开始)
- [三、common-core：统一响应与错误码](#三common-core统一响应与错误码)
- [四、common-core：通用工具类](#四common-core通用工具类)
- [五、common-core：耗时追踪与线程工具](#五common-core耗时追踪与线程工具)
- [六、common-security：数据脱敏](#六common-security数据脱敏)
- [七、generator：代码生成器](#七generator代码生成器)
- [八、与 Spring Boot starter 的关系](#八与-spring-boot-starter-的关系)
- [九、约定与踩坑](#九约定与踩坑)
- [十、测试现状](#十测试现状)

---

## 一、模块地图

```
common/                       独立 Gradle 工程
├── common-core              → com.jasper:common-core:1.0.0
│   ├── result / exception   统一响应、错误码、业务异常
│   ├── api / constants      IEnum、BaseConvert、ThreadType
│   ├── lang / io / utils    字符串/集合/日期/文件/JSON 等工具类
│   ├── monitor              TimeTracker 耗时追踪
│   ├── thread               线程池、调度器、可重置闩锁、服务线程
│   └── functional           可抛受检异常的函数式接口
├── common-security          → com.jasper:common-security:1.0.0
│   └── annotation / core    @Sensitive 注解、脱敏策略与字段名匹配器
└── generator                （不发布）MyBatis-Plus 代码生成器
```

| 模块 | 发布坐标 | 对外依赖 | 说明 |
|---|---|---|---|
| common-core | `com.jasper:common-core:1.0.0` | slf4j、Jackson 3、MapStruct | 基础工具与统一响应，几乎所有模块都依赖它 |
| common-security | `com.jasper:common-security:1.0.0` | common-core、Jackson 3 + Jackson 2 | 纯脱敏逻辑，web starter 以 `api` 方式传递依赖 |
| generator | 不发布 | MyBatis-Plus、Freemarker | 本地跑 `main` 生成 CRUD 代码 |

> 注意：工程内 `version = "1.0-SNAPSHOT"`，但发布坐标在各自 `build.gradle.kts` 的 `publishing` 块里**显式写成 `1.0.0`**。引用时用 `1.0.0`。

---

## 二、快速开始

### 构建与测试

```bash
cd common
./gradlew build                          # 构建全部模块并跑测试
./gradlew :common-core:test              # 只跑 core 测试
./gradlew :common-security:test          # 只跑脱敏测试
```

### 发布到本地 Maven（根工程据此引用）

**顺序不能反**：security 依赖 core，必须先发 core。

```bash
./gradlew :common-core:publishToMavenLocal
./gradlew :common-security:publishToMavenLocal
```

产物位于 `~/.m2/repository/com/jasper/{common-core,common-security}/1.0.0/`。

### 在其他模块引入

```kotlin
// Gradle Kotlin DSL
implementation("com.jasper:common-core:1.0.0")
implementation("com.jasper:common-security:1.0.0")   // 需要脱敏时
```

> common 工程的仓库列表默认只有 `mavenCentral()`，**不读 mavenLocal**。
> 因此本工程内部若依赖已发布的兄弟模块（如 common-security 依赖 common-core），
> 需要在该模块的 `repositories` 里显式加 `mavenLocal()`——common-security 已加。

---

## 三、common-core：统一响应与错误码

### 3.1 `ApiResult<T>` — 统一响应结构

`record`，`@JsonInclude(NON_NULL)`，字段：`code`（String）、`message`、`data`、`timestamp`、`traceId`。

```java
// 成功
ApiResult.success(user);
ApiResult.success();

// 失败：code 既可以是系统码也可以是业务码
ApiResult.error(ResultCode.BAD_REQUEST);
ApiResult.error("RB1001", "库存不足");
ApiResult.error("RB1001", "库存不足", extraData);
ApiResult.error(400, "参数错误");          // 兼容旧 int 调用的重载
```

- `code` 是 **String**：系统码是 `"400"` / `"500"`，业务码是 `"RB1001"` 形式。
- `success/error` 自动写入 `timestamp`（毫秒）和 `traceId`（从 MDC 的 `traceId` 读取，无则 `null`）。

### 3.2 错误码体系（三个接口 + 一个枚举）

| 类型 | 作用 | code 形态 | i18n key |
|---|---|---|---|
| `IEnum<T>` | 通用枚举协议，`getCode()` / `getDesc()`，带静态 `parseOf()` | 任意 | — |
| `IResultCode` | 系统码，`getCode()` 返回 Integer | `400` | 默认 `SYS400` |
| `IBusinessResultCode` | 业务码，继承 `IEnum<String>` | `RB1001` | 默认与 code 相同（`RB1001=stock not enough`） |
| `ResultCode` | 内置系统码枚举 | 200/400/401/403/404/405/412/500/503/50000/50001 | `SYS` + 数字 |

自定义业务码的推荐写法：

```java
public enum OrderCode implements IBusinessResultCode {
    STOCK_NOT_ENOUGH("RB1001", "库存不足");

    private final String code;
    private final String desc;
    // getCode()/getDesc() ...
}
// ApiResult.error(OrderCode.STOCK_NOT_ENOUGH, msg) → code = "RB1001"
```

`IEnum.parseOf(EnumClass, value)` 支持字符串 trim、忽略大小写、自动转数字类型，找不到时抛带候选值列表的 `IllegalArgumentException`。

### 3.3 `BusinessException` — 业务异常

三种构造来源，都携带 `i18nKey` 供国际化解析：

```java
throw new BusinessException(OrderCode.STOCK_NOT_ENOUGH);            // 业务码
throw new BusinessException(ResultCode.FORBIDDEN, "无权访问");       // 系统码
throw new BusinessException("ORDER_001", "旧式字符串码");           // 传统 String，兼容
```

辅助方法：`hasResultCode()`、`hasBusinessResultCode()`、`getNumericCode()`（系统码取枚举值，否则回落 400）。
> 兼容变化提醒：老式 `new BusinessException("ORDER_001", ...)` 现在 `ApiResult.code` 直接是 `"ORDER_001"`，不再是 400。

### 3.4 `FieldDifference`

对象字段对比结果：`tableName` / `fieldName` / `oldValue` / `newValue`。由 `ObjectUtils.compare()` 产出。

---

## 四、common-core：通用工具类

### 4.1 `lang` 包

| 类 | 主要方法 | 说明 |
|---|---|---|
| `StrUtil` | `stringTrim`、`isEmpty`、`bytes2string` | 字符串处理 |
| `ObjectUtils` | `isNull` / `isNotNull`、`isNullAll` / `isNotNullAll`、`isIn`、`isEquals` / `isNotEquals`、`compare(old,new)` | 空值判断兼容 String/Collection/Map；`compare` 用反射逐字段对比，返回 `List<FieldDifference>` |
| `ArrayUtils` | `isEmpty` / `isNotEmpty`、`getFirst` / `getLast`、`reverse`、`swap` | 数组工具 |
| `MapUtil` | `listToMap(list, keyExtractor)` | List 转 Map |
| `BigDecimalUtil` | `nullToZero`、`toBigDecimal` | 精度计算辅助 |
| `DateUtil` | `firstDayOfMonth`、`LastDayOfMonth`、`getDayOfMonth`、`getDiffDays`、`getLocalDate`、`isExceedLimit`、`isWithinOneYear` | 基于 `java.time.LocalDate` |
| `DateUtilsOld` | `firstDayOfMonth`、`LastDayOfMonth`、`getDayOfMonth` | 老版 `java.util.Date` 兼容实现，新代码优先用 `DateUtil` |

### 4.2 `io` / `utils` 包

| 类 | 主要方法 | 说明 |
|---|---|---|
| `FileUtil` | `mkdirs`、`getFileLines`、`copyFile`、`copyFileStream` | 目录创建、行数统计、文件/流拷贝 |
| `JacksonUtils` | `toJsonString(o[, include])`、`ParseJson(json, type[, include])` | 基于 Jackson 3（`tools.jackson`，`JsonMapper.builder()`）的 JSON 读写，`JsonInclude.Include` 注解仍用 `com.fasterxml.jackson.annotation`；三个静态单例 mapper 懒加载 |
| `AssignmentHelper` | `setIfNull(supplier, setter, default)`、`setIfChanged(target, setter, before, after)` | 空值安全赋值、仅当新旧值不同才赋值 |

### 4.3 `functional` 包

`CheckedRunnable`、`CheckedSupplier<T>`、`CheckedConsumer<T>`、`CheckedFunction<T,R>`——与 JDK 原生函数式接口对应，但允许 lambda 抛受检异常，配合 `TimeTracker.measure()` 使用，免去 `try/catch` 样板。

---

## 五、common-core：耗时追踪与线程工具

### 5.1 `TimeTracker` — 耗时追踪

`AutoCloseable`，嵌套层级用 `ThreadLocal` 维护，日志带缩进；基于 `System.nanoTime()`，不受系统时钟调整影响。

```java
// 方式一：try-with-resources，自动打 start + elapsed 两条日志
try (TimeTracker t = TimeTracker.start("queryDb")) {
    doQuery();
}

// 方式二：一行包住 lambda，异常原样透传（sneakyThrow，不改方法签名）
String r = TimeTracker.measure("callRemote", () -> remote.invoke());
TimeTracker.measure("task", () -> doSomething());   // CheckedRunnable

// 方式三：静默计时，不打日志，只取耗时（访问日志用）
TimeTracker t = TimeTracker.silent("access");
try { chain.doFilter(req, res); }
finally { log.info("cost={}ms", t.elapsedMillis()); t.close(); }
```

- `start(name)` 普通模式：构造打 `start`，`close` 打 `elapsed` 与 `success`，`fail()` 可标记失败。
- `silent(name)` 静默模式：**不打日志、不动层级 ThreadLocal**，`close()` 空操作，只提供 `elapsedMillis()`。Spring Boot 的 `AccessLogFilter` 就是用这个模式自行组织慢请求 WARN 日志。

### 5.2 `thread` 包

| 类 | 说明 |
|---|---|
| `ThreadType` | 枚举 `VIRTUAL` / `PLATFORM`，`getDefault()` = PLATFORM，`fromStringOrDefault()` 容错解析 |
| `ExecutorServiceFactory` | `newSingleThreadScheduledExecutor`、`newScheduledThreadPool`，可按 `ThreadType` 创建虚拟线程或平台线程池 |
| `SchedulerFactory` | 单例 `getInstance()`，`getScheduler()` 获取调度器，`reset()` 重建 |
| `CountDownLatch2` | 可重置闩锁：`await`（含超时版）、`countDown`、`getCount`、`reset` |
| `ServiceThread` | 抽象后台服务线程（`Runnable`）：实现 `getServiceName()`，提供 `start` / `shutdown` / `makeStop` / `wakeup` / `isStopped`、daemon 控制 |
| `ThreadLocalManager` | 按 key 管理 ThreadLocal：`set` / `get` / `contains` / `remove` / `snapshot` / `clear`，便于请求结束时统一清理 |

---

## 六、common-security：数据脱敏

**不依赖 Spring**，只依赖 common-core（`IEnum`）与 Jackson（3 + 2 双版本）。两种用法：注解式（DTO 序列化）和字段名匹配式（日志/异常）。

> Jackson 版本：common 库本身用 Jackson 3（3.2.2，`tools.jackson.*`）；但 Spring Boot 3.5 内置 Jackson 2，
> 所以 `@Sensitive` 同时打两套 `@JsonSerialize`（Jackson 2 的 `SensitiveSerializer` + Jackson 3 的 `SensitiveValueSerializer`），
> 两套 Jackson 都在 classpath 时各自生效，web starter 无需特殊配置。

### 6.1 `@Sensitive` 注解 + `SensitiveType` 策略

打在 DTO 字段上，Jackson 序列化时自动脱敏，无需全局配置：

```java
public class UserVO {
    @Sensitive                          // ******
    private String password;

    @Sensitive(SensitiveType.PHONE)     // 138****1234
    private String phone;

    @Sensitive(SensitiveType.ID_CARD)   // 1101**********1234
    private String idCard;
}
```

| 策略 | code | 规则 |
|---|---|---|
| `MASK` | `mask` | 全部 `******`（密码/token 默认） |
| `KEEP_EDGE` | `keep_edge` | 保留首尾各一位 |
| `PHONE` | `phone` | 11 位保留前 3 后 4，否则退化 KEEP_EDGE |
| `ID_CARD` | `id_card` | 18 位保留前 4 后 4，15 位保留前 3 后 4，否则退化 KEEP_EDGE |

`SensitiveType implements IEnum<String>`，`getCode()` 是上表 code，`getDesc()` 是中文描述，可用 `IEnum.parseOf(SensitiveType.class, "phone")` 解析。
`SensitiveType.mask(value, type)`：null / 空串原样返回，type 为 null 时按 MASK 处理。
两个序列化器：`SensitiveSerializer`（Jackson 2，实现 `ContextualSerializer`）与 `SensitiveValueSerializer`（Jackson 3，继承 `ValueSerializer`——Jackson 3 把 `JsonSerializer` 改名了）。都读取字段/类上的注解生成对应实例，**必须 new 新实例**，避免字段间配置串味。

### 6.2 `SensitiveFieldMatcher` — 按字段名匹配脱敏

用于访问日志、校验异常的 `rejectedValue` 这类**没有注解**的动态值：

```java
SensitiveFieldMatcher matcher = new SensitiveFieldMatcher(
        List.of("password", "token", "phone", "idCard"));

matcher.isSensitive("address.password");        // true（级联路径，任一段命中即可，忽略大小写）
matcher.mask("password", "123456");             // ******
matcher.mask("phone", "13812345678");           // 138****5678（按字段名推断策略）
matcher.mask("nickname", "jasper");             // jasper（非敏感字段原样返回）
```

- 字段名命中任意关键字即视为敏感；命中后按字段名推断策略：`phone`/`mobile` → PHONE，`idcard`/`idno`/`identity` → ID_CARD，其余 → MASK。
- 默认关键字列表由 Spring Boot 的 security starter 提供：`password, pwd, token, secret, accessKey, authorization, credentials, phone, mobile, idCard`（配置前缀 `jasper.security.sensitive.fields`）。在非 Spring 项目里直接把列表传进构造器即可。

---

## 七、generator：代码生成器

不发布的本地工具，基于 MyBatis-Plus `FastAutoGenerator` + Freemarker 模板，生成 entity / controller / mapper / service / serviceImpl 与 mapper XML。

- 入口：`jasper.Generator#main`
- 模板：`src/main/resources/templates/*.ftl`（支持 Java 与 Kotlin 版 entity）
- 使用前按目标库改 `Generator` 里的常量：`DATA_SOURCE_CONFIG`（URL/账号/密码）、`PROJECT_PATH`、`PARENT_PACKAGE`、`TABLE_NAME`。
- XML 通过 `pathInfo` 强行输出到 `src/main/resources/mappers`。

> 当前文件里写死了本地数据源和路径，属于个人开发环境配置，换机器需先改常量。

---

## 八、与 Spring Boot starter 的关系

`common` 是**纯 Java 库**；自动配置在根工程的 `spring-boot/` 下：

```
common/common-core ──────────→ 被所有模块依赖
common/common-security ─api──→ spring-boot/common-security-spring-boot-starter
                                        └─api─→ spring-boot/common-web-spring-boot-starter
```

- `common-security-spring-boot-starter`：只做自动配置，注册 `SensitiveProperties` / `SensitiveFieldMatcher` Bean，`jasper.security.enabled=false` 可关闭，用户自定义 matcher Bean 可覆盖。
- `common-web-spring-boot-starter`：全局异常处理、响应自动包装、访问日志、Jackson 约定、i18n、下游异常归一化；**校验异常的 rejectedValue 与访问日志参数共用同一个 `SensitiveFieldMatcher`**，密码/token 不会回显或落日志。

非 Spring 项目只用 `common` 即可，按需引坐标，无需 starter。

---

## 九、约定与踩坑

1. **Jackson 3 + Jackson 2 双版本共存**：common 库本身升级到 Jackson 3（`tools.jackson.*`，3.2.2），但 Spring Boot 3.5 内置 Jackson 2（`com.fasterxml.jackson.*`，2.19.2）。迁移踩过的坑：
   - 版本改了但依赖坐标没改会直接解析失败——Jackson 3 的坐标是 `tools.jackson.core:jackson-databind`，不是 `com.fasterxml.jackson.core`；
   - Jackson 3 把 `JsonSerializer` 改名为 `ValueSerializer`，`SerializerProvider` → `SerializationContext`，`ContextualSerializer` 接口并入 `ValueSerializer.createContextual()`；
   - `JsonInclude` 注解仍在 `com.fasterxml.jackson.annotation`，没换包；异常从受检的 `JsonProcessingException` 变成 unchecked 的 `JacksonException`；
   - `@Sensitive` 因此打两套 `@JsonSerialize`，配两个序列化器类，保证 Spring Boot（Jackson 2）和独立 Jackson 3 场景都脱敏。
2. **对外可见类型用 `api` 而非 `implementation`**：common-security 里 `SensitiveType implements IEnum`、两套序列化器与注解，这些类型对使用者可见，所以 common-core 和两套 Jackson 都声明为 `api`；否则发布 POM 里是 runtime scope，单独引用时编译期找不到类型。
3. **`mavenLocal()` 要显式加**：common 工程子模块默认只查中央仓库，依赖已发布的兄弟模块必须加 `mavenLocal()`。
4. **i18n 分两个 basename**：系统码 `i18n/system`（`SYS400=...`、`VALIDATION_BODY=...`），业务码 `i18n/business`（`RB9999=stock not enough`）。`ResourceBundleMessageSource` 默认 `fallbackToSystemLocale=true`，测试机 Locale 为英文时会命中 `system_en.properties` 而非中文基础文件，自动配置里已关闭该回退。
5. **`ApiResult.code` 是 String**：所有断言和前端解析按字符串处理。
6. **`TimeTracker` 优先**：需要计时先用 `TimeTracker`，不要用 `System.currentTimeMillis()`；访问日志这类自行组织日志的场景用 `silent()`。

---

## 十、测试现状

| 模块 | 用例数 | 状态 |
|---|---|---|
| common-core | 31 | 29 通过、2 个历史失败：`FileUtilTest#test`（读取测试文件字符集 `MalformedInputException`）、`IEnumTest#testParse`（`IllegalArgumentException`）；均与近期改动无关。Jackson 3 迁移后 `JsonUtilTest` 已加断言并通过 |
| common-security | 8 | 全绿（策略 2、字段名匹配 4、注解序列化双 Jackson 2 个） |
| generator | 无单测 | 本地运行验证 |

> 发布前若只需验证改动部分，可用 `./gradlew :common-core:test --tests '全限定类名'` 精确执行，再单独 `publishToMavenLocal`。
