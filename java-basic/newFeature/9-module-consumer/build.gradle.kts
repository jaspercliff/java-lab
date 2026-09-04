plugins {
    id("java-library-convention")
}

dependencies {
    implementation(project(":java-basic:newFeature:9-module-producer"))
}

tasks.withType<JavaCompile>().configureEach {
    options.compilerArgs.addAll(listOf("--module-path", classpath.asPath))
}
