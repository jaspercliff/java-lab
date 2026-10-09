plugins {
    id("spring-boot-convention")
}

dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(platform(libs.bom.alibaba))
    implementation(platform(libs.bom.springcloud))
    implementation(libs.resilience4j)
    implementation(libs.boot3.resilience4j)
    implementation(libs.spring.aop)
    implementation(libs.spring.boot.starter.actuator)
}