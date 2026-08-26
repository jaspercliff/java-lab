plugins {
//    java = 自己编译、运行的 Java 项目。
//    java-library = 是一个给别人依赖的 Java 库，因此需要 api / implementation 这套依赖边界
    `java-library`
}

repositories {
    mavenCentral()
}

tasks.withType<Test> {
    useJUnitPlatform()
}