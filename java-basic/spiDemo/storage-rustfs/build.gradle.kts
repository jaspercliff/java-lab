
plugins {
    id("java-library-convention")
}

dependencies {
//    project()告诉 Gradle 你要引用的不是远程仓库（如 Maven Central）里的 Jar 包，而是本地代码库中的另一个模块
//    :根路径 ： 路径分隔符
    implementation(project(":java-basic:spiDemo:storage-api"))
}
