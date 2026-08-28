plugins {
    // java-library：对外可被依赖的库，提供 api / implementation 依赖边界
    `java-library`
}



repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

subprojects {
    if (project.path != ":base") {
        plugins.withId("java-library") {
            dependencies {
                add("implementation", project(":base"))
            }
        }
    }
}

dependencies {
    // Use JUnit Jupiter for testing.
//    testImplementation(libs.junit.jupiter)
//
//    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
//
//    // This dependency is used by the application.
//    implementation(libs.guava)
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.named<Test>("test") {
    // Use JUnit Platform for unit tests.
    useJUnitPlatform()
}