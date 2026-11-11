plugins {
    id("java")
}

group = "com.jasper"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.slf4j.api) //MDC
    compileOnly(libs.jakarta.servlet)
    implementation(platform(libs.spring.framework.bom))
    implementation(libs.spring.context)
    implementation(libs.spring.boot.autoconfigure)
    implementation(libs.spring.web)
    implementation(platform(libs.spring.boot.dependencies))
    implementation(libs.spring.boot)

    annotationProcessor(platform(libs.spring.boot.dependencies))
//    让 IDE 拥有“配置提示”与“自动补全”
    annotationProcessor(libs.spring.boot.configuration.processor)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.jakarta.servlet)
    testRuntimeOnly(libs.junit.platform.launcher)

}

tasks.test {
    useJUnitPlatform()
}