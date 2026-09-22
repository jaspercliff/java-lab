plugins {
    id("java")
}

group = "com.jasper"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.jdbc)
    implementation(libs.mysql.connector.j)
    implementation(libs.mybatis.plus)

}

tasks.test {
    useJUnitPlatform()
}