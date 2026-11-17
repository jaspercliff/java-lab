plugins {
    id("spring-boot-convention")
}

dependencies {
    implementation(libs.spring.boot.starter.web)
    // Bean Validation：spring-boot-starter-web 从 Boot 2.3 起不再传递 validation，
    // 必须显式引入；它传递 jakarta.validation-api + hibernate-validator(+ EL)。
    implementation(libs.spring.boot.starter.validation)
    implementation(libs.springdoc.openapi.starter.webmvc.ui)
    implementation(project(":spring-boot:common-trace-spring-boot-starter"))
}

// spring-boot-convention 只带 spring-boot-starter-test，没有开启 JUnit Platform
tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
