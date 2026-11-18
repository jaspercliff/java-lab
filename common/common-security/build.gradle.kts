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
    // 对外可见类型：SensitiveType implements IEnum、@Sensitive 引用 Jackson 注解
    api("com.jasper:common-core:1.0.0")
    api(libs.jackson.databind)

    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.bundles.junit)
    testImplementation(libs.assertj)
    runtimeOnly(libs.logback.classic)
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
