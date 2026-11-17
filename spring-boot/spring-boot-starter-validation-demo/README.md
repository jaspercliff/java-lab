# spring-boot-starter-validation-demo

启动：`./gradlew :spring-boot:spring-boot-starter-validation-demo:bootRun`（端口 18091），
接口用例见 `demo.http`，测试 `./gradlew :spring-boot:spring-boot-starter-validation-demo:test`。

## 1. 约束注解速查（全在 `UserCreateRequest`）

| 类别 | 注解 | 说明 |
|---|---|---|
| 空值 | `@NotNull` / `@NotBlank` / `@NotEmpty` | 对象 / 字符串(trim 后) / 集合数组 Map |
| 长度范围 | `@Size` / `@Min` / `@Max` / `@DecimalMin` / `@Digits` / `@Positive` | 数字用 `@Min` 别用 `@Size` |
| 格式 | `@Email` / `@Pattern` / `@URL`(HV) / `@Past` / `@Future` | `@Email` 允许 `""`（空值交给 `@NotBlank`） |
| 逻辑 | `@AssertTrue` / `@AssertFalse` | getter 上写，方法名 `isXxx()` |

要点：约束默认**接受 null**（除 `@NotNull`），所以「非空」和「格式」要分开写，否则空值会同时报两条。

## 2. 三大触发入口（异常类型不同，见 `GlobalExceptionHandler`）

| 写法 | 触发 | 异常 | message 前缀 |
|---|---|---|---|
| Controller 方法参数 `@Valid @RequestBody` | 请求体 Bean | `MethodArgumentNotValidException` | `VALIDATION_BODY` |
| 类上无 `@Validated`、参数上直接写 `@Min` 等（Spring 6.1+ 内置） | `@RequestParam/@PathVariable` | `HandlerMethodValidationException` | `VALIDATION_PARAM` |
| 类上 `@Validated` + 方法参数约束（Service 或 Controller） | AOP 拦截方法 | `ConstraintViolationException` | `VALIDATION_METHOD` |


## 3. 级联与集合

- 嵌套对象字段上加 `@Valid`：`address.city` 这种带点号的字段路径来自 BindingResult；
- 集合元素级联要写在泛型上：`List<@Valid Address> shippingAddresses`；
- 不写 `@Valid` 时嵌套对象内部约束**完全不会触发**——这是最高频的坑。

## 4. 分组 / 组序列

- 约束的 `groups` 属性指定归属；DTO 字段不写 groups 则属于 `Default`；
- `@Validated(CreateGroup.class)` 只跑指定组，**Default 组的约束不再执行**（不是「追加」而是「替换」）；
- `@GroupSequence({Default.class, CreateGroup.class, UpdateGroup.class})` 定义分阶段校验：
  前一组有错后一组直接跳过，避免用户还没填就报一堆格式错；
- 组序列不能循环，且不能在序列里把 `Default` 排在自定义组之后又让自定义组继承 `Default`。

## 5. 自定义约束（3 种）

1. **字段级正则** `Phone` + `PhoneValidator`：约束注解三要素 `message/groups/payload`，
   `@Constraint(validatedBy=...)` 指定校验器；`nullable()` 属性控制是否放行空值。
2. **类级跨字段** `DateRange`：标在 TYPE 上，校验器拿到整个对象，用反射比较 `activeFrom/activeTo`；
   `context.buildConstraintViolationWithTemplate(...).addPropertyNode("activeTo")` 把错误挂到具体字段。
3. **查库型** `UniqueUsername`：校验器标 `@Component` + 构造器注入 `UserService`，
   由 SpringConstraintValidatorFactory 从容器创建。校验器**无状态**，不要存请求级字段。

## i18n

- **i18n**：注解写 `{key}` 占位符 → `ValidationConfig` 把 Spring `MessageSource` 挂到 Validator
  → `i18n/messages.properties`（中文兜底）/ `messages_zh_CN.properties` / `messages_en.properties`
  按 `Accept-Language` 切换。`BindingResult` 路径会带 locale，`ConstraintViolationException` 路径需要 Spring MVC 传 locale。

