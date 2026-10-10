plugins {
    id("spring-boot-convention")
    id("com.gorylenko.gradle-git-properties") version "4.0.1"
}

dependencies {
    implementation(libs.spring.boot.starter.web)
//    官方提供的生产就绪功能模块，主要用于监控和管理应用程序。它提供端点、健康检查、指标采集等功能
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.admin.starter.client)
    implementation(libs.spring.boot.starter.cache)
    implementation(libs.spring.boot.starter.integration)
    implementation(libs.micrometer.registry.prometheus)
}

version = "1.0.0-snapshot"
//在构建时生成：META-INF/build-info.properties
springBoot {
    buildInfo()
}