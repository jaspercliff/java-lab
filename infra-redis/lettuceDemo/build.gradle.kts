plugins {
    id("java-library-convention")
}

dependencies {
    testImplementation(platform(libs.testcontainers.bom))
    testImplementation(libs.bundles.testcontainers)

    implementation(libs.lettuce.core)
}
