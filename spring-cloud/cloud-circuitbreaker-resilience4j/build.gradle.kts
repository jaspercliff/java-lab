plugins {
    id("spring-boot-convention")
}

dependencies {
    implementation(libs.spring.boot.starter.web)
//    When used with Spring Boot, it is necessary to include AOP.
    implementation(libs.spring.boot.starter.aop)
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.admin.starter.client)
    implementation(libs.spring.boot.starter.test)

    implementation(platform(libs.bom.alibaba))
    implementation(platform(libs.bom.springcloud))
    implementation(libs.openfeign)
    implementation(libs.cloud.resilience4j)
}
