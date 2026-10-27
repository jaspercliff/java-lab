plugins {
    java
}

val libs = extensions
    .getByType<VersionCatalogsExtension>()
    .named("libs")

dependencies {
    testImplementation(
        platform(libs.findLibrary("junit-bom").get())
    )
    testImplementation(libs.findLibrary("junit-jupiter").get())

    testRuntimeOnly(
        libs.findLibrary("junit-platform-launcher").get()
    )
    testImplementation(libs.findLibrary("assertj").get())
    testImplementation(libs.findLibrary("mockito-core").get())
    testImplementation(libs.findLibrary("mockito-junit-jupiter").get())

}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
