plugins {
    id("java-library-convention")
    // bootRun / bootJar 等能力
    id("org.springframework.boot")
}

    val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

    dependencies {
        implementation(platform(libs.findLibrary("spring-boot-dependencies").get()))
        testImplementation(libs.findLibrary("spring-boot-starter-test").get())
    }
