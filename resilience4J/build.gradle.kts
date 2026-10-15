plugins {
    id("java-library-convention")
}

dependencies {
    implementation(libs.resilience4j.all)
    implementation(libs.resilience4j.consumer)
//    implementation(libs.spring.boot.starter.web)
//    implementation(platform(libs.bom.alibaba))
//    implementation(platform(libs.bom.springcloud))
//    implementation(libs.resilience4j)
//    implementation(libs.spring.aop)
//    implementation(libs.spring.boot.starter.actuator)
}