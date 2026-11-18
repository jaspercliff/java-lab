plugins {
    id("java-library-convention")
}

group = "com.jasper"
version = "1.0.0"

dependencies {
    // 版本由 Spring Boot BOM 统一管理，与 trace starter 保持同一套依赖声明方式
    implementation(platform(libs.spring.boot.dependencies))

    // 全局异常处理依赖 spring-webmvc + jackson；参数校验异常依赖 hibernate-validator
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.jackson.datatype.jsr310)

    // ApiResult / ResultCode / BusinessException 来自 common-core，需对使用者可见
    api("com.jasper:common-core:1.0.0")

    // 脱敏匹配器与 @Sensitive 由 security starter 提供，web 的异常处理/访问日志直接复用
    api(project(":spring-boot:common-security-spring-boot-starter"))

    // Feign 异常归一化：可选依赖，使用方引入 openFeign 时才装配对应 advice
    compileOnly(libs.feign.core)

    // 让 IDE 拥有配置项提示与自动补全
    annotationProcessor(platform(libs.spring.boot.dependencies))
    annotationProcessor(libs.spring.boot.configuration.processor)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.feign.core)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
