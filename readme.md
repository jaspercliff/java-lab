# readme 

## base 

- 整个工程通用类

## build logic

gradle 依赖管理 

## common 

- 单独一个工程 通用模块 其他项目也可以导入
- **完整说明书见 [`common/readme.md`](common/readme.md)**（模块地图、工具类清单、脱敏用法、发布与踩坑）
- `common-core`：`ApiResult.code` 改为 String，系统码为 "400"/"500"，业务码为 "RB1001" 形式
- `IBusinessResultCode extends IEnum<String>`：`getCode()`=业务码、`getDesc()`=默认描述、`getMsg()`=i18n key；
  `IResultCode` 默认 `getI18nKey()` = "SYS" + 数字码。业务码与系统码分开两套枚举
- `BusinessException` 支持系统码枚举、业务码枚举、传统 String code 三种构造，均携带 i18nKey
- `common-security`：脱敏纯逻辑（`@Sensitive` / `SensitiveType` / `SensitiveFieldMatcher`），不依赖 Spring；
  `SensitiveType implements IEnum<String>`；发布坐标 `com.jasper:common-security:1.0.0`
- `TimeTracker` 新增 `silent(name)` 静默计时与 `elapsedMillis()`，供访问日志这类需要自定义日志级别的场景复用

## container 

- docker-compose-support使用的容器

## dataAccess 

- jdbc 
- mybatis (PostgreSQL，原生 MyBatis 全知识点；Flyway 编程式 API + `afterMigrate.sql` 回调管理脚本)
- mybatis-plus-demo (PostgreSQL，MyBatis-Plus 全知识点：注解/CRUD/Wrapper/分页/插件/主键/枚举/TypeHandler/SQL 注入器等)
- postgres-demo (JDBC + Flyway，V1/V2)
- jpa-demo (Flyway 骨架，待补 JPA 知识点)
- sharding

> 脚本约定：所有 demo 的建表 / 种子脚本统一由 Flyway 版本化管理（`db/migration/V*.sql`），
> 不再使用 `spring.sql.init` 或手写 JDBC 脚本解析。多个模块共用 `java_lab` 库时各自使用
> 独立的 schema history 表（`spring.flyway.table` / `Flyway.configure().table(...)`），
> 并配 `baseline-on-migrate: true` + `baseline-version: 0`；共享库禁止 `flyway.clean()`。

## dataStructure 

- 数据结构

## infra-redis 

- jedis-spring-data-redis 
- jedisDemo 使用的testContainer(运行一个 JUnit 测试类 → 自动启动 Redis Docker 容器 → 测试 → 自动停止/清理容器 )


## spring-boot 

### common-trace-spring-boot-starter

- 全局traceId 

### common-security-spring-boot-starter

- 只是 Spring Boot 自动配置层：装配 `SensitiveFieldMatcher` 并绑定配置；
  纯脱敏逻辑在 common 工程的 `common-security` 模块，本 starter 以 `api` 依赖它
- `jasper.security.enabled=false` 关闭自动配置，用户自定义 matcher Bean 可覆盖
- 默认关键字 `password,pwd,token,secret,accessKey,authorization,credentials,phone,mobile,idCard`，
  用 `jasper.security.sensitive.fields` 覆盖
- 使用：`implementation(project(":spring-boot:common-security-spring-boot-starter"))`；
  非 Spring 项目直接用 `com.jasper:common-security:1.0.0`

### common-web-spring-boot-starter

- 错误文案国际化：`WebI18nAutoConfiguration` 注册 MessageSource，basename 默认 `i18n/system`（SYS400、VALIDATION_BODY 等）
  与 `i18n/business`（RB9999=stock not enough），按 Accept-Language 切换；
  可用 `jasper.web.i18n.basenames` / `.encoding` 覆盖，key 不存在时回退默认文案
- ApiResult.code 为字符串：业务异常返回 RB1001 这类业务码，系统异常返回 "400"/"500"
- 校验异常的 `rejectedValue` 经 security starter 脱敏，密码/token 不回显；访问日志复用同一份敏感字段配置
- 全局异常处理，统一返回 common 的 `ApiResult`：业务异常、三类参数校验异常、请求体/参数错误、
  405/404/413/415、下游调用 502/503，兜底 500；500 默认不暴露异常 message（日志始终打完整堆栈）
- 成功响应自动包装 `ApiResult.success`，`@IgnoreApiResult` 可跳过；ResponseEntity/文件/SSE 自动跳过
- 请求访问日志（方法/URI/脱敏参数/状态码/耗时/traceId），耗时复用 common-core 的 `TimeTracker.silent()`（nanoTime），慢请求与 5xx 走 WARN
- Jackson 全局约定：`yyyy-MM-dd HH:mm:ss`、`Asia/Shanghai`、Long/BigInteger 转字符串、忽略 null
- 只装配在 Servlet Web 应用；总开关 `jasper.web.enabled=false`，
  子开关 `jasper.web.response.enabled`、`jasper.web.access-log.enabled`、`jasper.jackson.enabled`；
  自定义 Bean 可覆盖各能力，`jasper.web.expose-exception-message=true` 放开 500 文案
- 使用：`implementation(project(":spring-boot:common-web-spring-boot-starter"))`

### spring-boot-starter-jdbc-demo 

- jdbcTemplate and jdbcClient

### spring-boot-starter-validation-demo

- Bean Validation 全知识点：jakarta 约束注解、@Valid 级联、分组/组序列、@Validated 方法级校验、
  自定义约束（字段级 + 类级跨字段）、ConstraintValidator 注入 Spring Bean、i18n 错误文案、
  fail-fast、全局异常处理（MethodArgumentNotValidException / ConstraintViolationException 等）

### springdoc-demo

- springdoc-openapi 2.10.0 + Swagger UI（对应 Spring Boot 3.5.x；3.x 才对应 Boot 4），页面 `/swagger-ui.html`，描述 `/v3/api-docs` 与 `.yaml`
- 两个 Controller：`KnowledgeController`（实战写法，仅必要注解）+ `OpenApiAdvancedController`（高级注解演示），覆盖 springdoc / OpenAPI 全部注解、扩展点与配置项
- 所有响应统一为 common 工程的 `ApiResult<T>`，并引入 `common-trace-spring-boot-starter` 写入 MDC traceId
- 知识点索引、版本坑与实测结论见模块内 `readme.md`

### springdoc-xh-generator

- 读取 springdoc 的 `/v3/api-docs`，生成 xh 请求脚本（脚本保持简洁：打开即见 `xh` 命令）：
  一个 Controller/tag 一个目录，GET 查询合并到该目录的 `queries.sh`（交互选择或命令行传参），
  其余维护请求一接口一脚本、JSON body 用 `jaq -n` 整块生成。详见该模块 README。

