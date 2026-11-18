plugins {
    `java-library`
    id("io.freefair.lombok") version "9.5.0"
    `maven-publish`
}

group = "com.jasper"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    // 对外可见类型：SensitiveType implements IEnum、@Sensitive 引用两套 Jackson 注解
    api("com.jasper:common-core:1.0.0")
    api(libs.jackson.databind)
    // Spring Boot 3.5 内置 Jackson 2，@Sensitive 需要兼容两套序列化器
    api(libs.jackson2.databind)
    // 日志脱敏 Converter 继承 Logback 类，编译期需要；是否启用由使用方的日志实现决定，
    // 故用 compileOnly，不强制把 Logback 传递给不需要日志脱敏的使用方。
    compileOnly(libs.logback.classic)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.bundles.junit)
    testImplementation(libs.assertj)
    testImplementation(libs.logback.classic)
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            groupId = "com.jasper"
            artifactId = "common-security"
            version = "1.0.0"
        }
    }
}
