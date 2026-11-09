plugins {
    id("spring-boot-convention")
}

dependencies {
    implementation(libs.spring.boot.starter.web)
    // 提供应用健康检查、指标端点等
    implementation(libs.spring.boot.starter.actuator)
    // 将 Micrometer 指标暴露为 Prometheus 格式
    implementation(libs.micrometer.registry.prometheus)

}
