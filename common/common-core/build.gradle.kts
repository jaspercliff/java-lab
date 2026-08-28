plugins {
    id("java")
}

group = "com.jasper"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.bundles.junit)

    var lombok = libs.lombok
    compileOnly(lombok)
    annotationProcessor(lombok)
    testCompileOnly(lombok)
    testAnnotationProcessor(lombok)
}

tasks.test {
    useJUnitPlatform()
}