plugins {
    id("java-library-convention")
    id("java-test-convention")
}

dependencies {
    testImplementation(platform(libs.testcontainers.bom))
    testImplementation(libs.bundles.testcontainers)

    implementation(libs.lettuce.core)
}
