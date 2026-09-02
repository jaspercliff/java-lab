plugins {
    id("java")
    id("io.freefair.lombok") version "9.5.0"
    `maven-publish`
}

group = "com.jasper"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.bundles.junit)
    runtimeOnly(libs.logback.classic)
    implementation(libs.slf4j.api)

    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)
    annotationProcessor(libs.mapstruct.lombok.binding)

    implementation(libs.jackson.databind)
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            // 定义你的坐标，供其他项目引用
            groupId = "com.jasper" // 替换为你的组织ID
            artifactId = "common-core"
            version = "1.0.0"       // 替换为你的版本号
        }
    }
}