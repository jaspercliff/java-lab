plugins {
    id("spring-boot-convention")
}


dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.jdbc)
    runtimeOnly(libs.mysql.connector.j)
}
