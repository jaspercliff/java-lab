plugins {
    id("java-library-convention")
}

group = "com.jasper"
version = "1.0.0"

dependencies {
    // 版本由 Spring Boot BOM 统一管理
    implementation(platform(libs.spring.boot.dependencies))
    implementation(libs.spring.boot.autoconfigure)

    api("com.jasper:common-security:1.0.0")

    // 让 IDE 拥有配置项提示与自动补全
    annotationProcessor(platform(libs.spring.boot.dependencies))
    annotationProcessor(libs.spring.boot.configuration.processor)

    testImplementation(libs.spring.boot.starter.test)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
