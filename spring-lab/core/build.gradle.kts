plugins {
    id("java-library-convention")
}

dependencies {
    implementation(platform(libs.spring.framework.bom))
    implementation(libs.spring.context)
}
