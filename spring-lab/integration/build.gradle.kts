plugins {
    id("spring-boot-convention")
}

dependencies {
    implementation(platform(libs.spring.framework.bom))
    implementation(libs.spring.boot.starter.web)

}