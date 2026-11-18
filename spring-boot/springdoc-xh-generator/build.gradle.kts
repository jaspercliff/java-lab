plugins {
    id("java-library-convention")
    id("java-test-convention")
    // 独立 CLI 工具：提供 run / installDist，不依赖 spring-boot 插件
    application
}

dependencies {
    // OpenAPI 是 JSON，用 Jackson 3 解析成 JsonNode（无需引入 swagger-parser 整套模型）
    implementation(libs.jackson.databind)
}

application {
    mainClass = "com.jasper.xhgen.XhGeneratorCli"
}
