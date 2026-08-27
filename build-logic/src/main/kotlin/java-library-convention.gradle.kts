plugins {
    // java-library：对外可被依赖的库，提供 api / implementation 依赖边界
    `java-library`
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

dependencies {
    val lombok = libs.findLibrary("lombok").get()
    // Lombok 仅编译期使用，不要进 runtime
    compileOnly(lombok)
    annotationProcessor(lombok)
    testCompileOnly(lombok)
    testAnnotationProcessor(lombok)

    implementation(libs.findLibrary("guava").get())
    // 日志门面：具体实现（logback 等）由应用模块自行引入
    implementation(libs.findLibrary("slf4j-api").get())
    runtimeOnly(libs.findLibrary("logback-classic").get())

    testImplementation(libs.findLibrary("junit-jupiter").get())
    testRuntimeOnly(libs.findLibrary("junit-platform-launcher").get())
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}
