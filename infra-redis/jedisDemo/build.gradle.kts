plugins {
    id("java-library-convention")
    id("java-test-convention")
}

dependencies {
    implementation(libs.jedis)

    testImplementation(platform(libs.testcontainers.bom))
    testImplementation(libs.bundles.testcontainers)

}
