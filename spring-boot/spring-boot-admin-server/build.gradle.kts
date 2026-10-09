plugins {
    id("spring-boot-convention")
    id("com.gorylenko.gradle-git-properties") version "4.0.1"
}

dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.admin.starter.server)
}

version = "1.0.0-snapshot"
//在构建时生成：META-INF/build-info.properties
springBoot {
    buildInfo()
}