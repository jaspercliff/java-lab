plugins {
    id("spring-boot-convention")
}

dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.docker.compose)
    implementation(libs.spring.boot.starter.data.redis)
}
