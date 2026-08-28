plugins {
    id("java")
}

group = "com.jasper"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {

    implementation(libs.mysql.connector.j)
    implementation(libs.mybatis.plus)
    implementation(libs.mybatis.plus.generator)
    implementation(libs.freemarker)
}

tasks.test {
    useJUnitPlatform()
}