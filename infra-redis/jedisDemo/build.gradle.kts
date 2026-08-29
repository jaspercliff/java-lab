plugins {
    id("java-library-convention")
}

dependencies{
    implementation(libs.jedis)

//    testImplementation(platform(libs.testcontainers.bom))
//    testImplementation(libs.bundles.testcontainers)

    testImplementation(platform(libs.testcontainers.bom))
    testImplementation(libs.testcontainers.junit.jupiter)
    testImplementation(libs.testcontainers.testcontainers)
}
