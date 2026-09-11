plugins {
    id("java-library-convention")
}

dependencies {
    implementation(platform(libs.spring.framework.bom))
    implementation(libs.spring.context)
    implementation(libs.spring.tx)
    // 原本属于 Java EE Annotation API；Java 8 的 JDK 曾经把它带进来了，Java 11 移除，后来 Java EE 又演变成 Jakarta EE
    implementation(libs.jakarta.annotation)
}
