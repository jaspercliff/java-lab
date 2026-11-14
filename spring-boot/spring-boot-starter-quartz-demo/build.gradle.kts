plugins {
    // 复用工程内的 Spring Boot 约定：Java 21、Spring Boot BOM、bootRun/bootJar、测试依赖
    id("spring-boot-convention")
}

dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(project(":spring-boot:common-trace-spring-boot-starter"))

    // Quartz 与 Spring Boot 自动配置：
    // - 自动创建 SchedulerFactoryBean / Scheduler
    // - 读取 spring.quartz.* 配置
    // - 支持 memory / jdbc 两种 JobStore
    implementation(libs.spring.boot.starter.quartz)

    // JDBC JobStore（application-jdbc.yml）需要 DataSource、事务管理器与 MySQL 驱动。
    // 默认 memory 模式下不会创建 DataSource，因此不影响开箱即用。
    implementation(libs.spring.boot.starter.jdbc)
    runtimeOnly(libs.mysql.connector.j)

    // Gradle 9 需要显式提供 JUnit Platform Launcher，版本由 Spring Boot BOM 管理
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.test {
    // 本模块使用 JUnit 5；spring-boot-convention 没有默认开启 JUnit Platform
    useJUnitPlatform()
}
