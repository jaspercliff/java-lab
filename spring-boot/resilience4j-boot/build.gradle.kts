plugins {
    id("spring-boot-convention")
}

dependencies {
    implementation(libs.spring.boot.starter.web)
//    When used with Spring Boot, it is necessary to include AOP.
    implementation(libs.resilience4j.spring.boot3)
    implementation(libs.spring.boot.starter.aop)
    implementation(libs.spring.boot.starter.actuator)
}
