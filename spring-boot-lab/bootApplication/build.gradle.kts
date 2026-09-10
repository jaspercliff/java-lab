plugins {
    id("spring-boot-convention")
}

dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.jdbc)
    implementation(libs.mysql.connector.j)
    implementation(libs.mybatis.plus)

    implementation(project(":spring-boot-lab:jasper-starter:jasper-spring-boot-starter"))
}
