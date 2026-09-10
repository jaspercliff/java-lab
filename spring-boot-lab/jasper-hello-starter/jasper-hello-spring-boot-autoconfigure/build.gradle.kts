plugins {
    id("java-library-convention")
}

dependencies {
    api(platform(libs.spring.boot.dependencies))
    api(libs.spring.boot.autoconfigure)

    annotationProcessor(platform(libs.spring.boot.dependencies))
    annotationProcessor(libs.spring.boot.configuration.processor)
}