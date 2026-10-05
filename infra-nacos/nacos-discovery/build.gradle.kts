plugins {
    id("spring-boot-convention")
}

dependencies {
    implementation(libs.spring.boot.starter.web)
//    implementation(libs.spring.boot.starter.jdbc)
//    implementation(libs.mysql.connector.j)
//    implementation(libs.mybatis.plus)


    implementation(platform(libs.bom.alibaba))
    implementation(platform(libs.bom.springcloud))
    implementation(libs.nacos.discovery)
    implementation(libs.openfeign)
    implementation(libs.loadbalancer)

}
