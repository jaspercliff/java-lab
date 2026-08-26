plugins {
    id("spring-boot-convention")
}

group = "com.jasper"
version = "unspecified"

dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.jdbc)

    runtimeOnly(libs.mysql.connector.j)
}
