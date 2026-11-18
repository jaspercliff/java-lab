plugins {
    id("spring-boot-convention")
}

dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(project(":spring-boot:jasper-starter:jasper-spring-boot-starter"))
    implementation(project(":spring-boot:common-web-spring-boot-starter"))
    implementation(project(":spring-boot:common-trace-spring-boot-starter"))
}
