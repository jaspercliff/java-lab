plugins {
    id("spring-boot-convention")
}

dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(libs.springdoc.openapi.starter.webmvc.ui)
}

// spring-boot-convention 只带 spring-boot-starter-test，没有开启 JUnit Platform
tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
