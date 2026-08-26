plugins {
    id("java-library-convention")
    // bootRun bootJar 等能力
    alias(libs.plugins.spring.boot)
}

group = "com.jasper"
version = "0.0.1-SNAPSHOT"

java {
    toolchain { languageVersion = JavaLanguageVersion.of(21) }
}

repositories { mavenCentral() }

dependencies {

    implementation(platform(libs.spring.boot.dependencies))
    implementation(libs.spring.boot.starter.web)
    implementation(libs.spring.boot.starter.jdbc)
    implementation(libs.mybatis.plus)


    implementation(libs.lombok)
    annotationProcessor(libs.lombok)


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


    testImplementation(libs.spring.boot.starter.test)
}

tasks.withType<Test> { useJUnitPlatform() }
