plugins {
    id("spring-boot-convention")
}

group = "com.jasper"
version = "0.0.1-SNAPSHOT"

dependencies {
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.jdbc)
    implementation(libs.mybatis.plus)



    implementation(libs.sharding.jdbc)
    // jdbc:shardingsphere:classpath:、!SHARDING、Hikari、MySQL 方言、Standalone 模式均为可选插件
    implementation(libs.sharding.infra.url.classpath)
    implementation(libs.sharding.core)
    implementation(libs.sharding.hikari)
    implementation(libs.sharding.mysql.dialect)
    implementation(libs.sharding.standalone.mode)
    implementation(libs.sharding.standalone.repo.memory)
    implementation(libs.sharding.authority.simple)
    runtimeOnly(libs.mysql.connector.j)
}
