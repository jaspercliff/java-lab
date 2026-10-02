plugins {
    id("spring-boot-convention")
}

dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.webflux)

    implementation(platform(libs.bom.alibaba))
    implementation(platform(libs.bom.springcloud))
    implementation(libs.openfeign)
    implementation(libs.loadbalancer)
}