plugins {
    id("spring-boot-convention")
}

dependencies {
    implementation(libs.spring.boot.starter.web)
    // Bean Validation：springdoc 会把 @NotNull/@Size/@Pattern 等约束自动翻译成 schema 的
    // required / minLength / maxLength / pattern / minimum / maximum
    implementation(libs.spring.boot.starter.validation)

    // Spring Boot 3.5.x 必须用 springdoc 2.x（3.x 只支持 Boot 4）。
    // 这一个 starter = springdoc-openapi-starter-webmvc-api（生成 /v3/api-docs）
    //                + springdoc-openapi-starter-webmvc-ui（渲染 /swagger-ui.html）
    implementation(libs.springdoc.openapi.starter.webmvc.ui)

    // 统一响应 com.jasper.result.ApiResult 会从 MDC 读取 traceId；
    // 按工程约定，所有 Spring Boot 应用都要引入 trace starter 来写入 MDC。
    implementation(project(":spring-boot:common-trace-spring-boot-starter"))

    // 只要 JSON/YAML、不要 UI 时换成下面这个（与上面的 ui starter 二选一）：
    // implementation(libs.springdoc.openapi.starter.webmvc.api)
}

// spring-boot-convention 只带 spring-boot-starter-test，没有开启 JUnit Platform
tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
