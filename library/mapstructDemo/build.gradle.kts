plugins {
    id("java-library-convention")
}

dependencies{

    implementation(libs.mapstruct)
    annotationProcessor(libs.mapstruct.processor)
    annotationProcessor(libs.mapstruct.lombok.binding)
}