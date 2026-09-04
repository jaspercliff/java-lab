
plugins {
    id("java-library-convention")
}

dependencies {
    implementation(project(":java-basic:spiDemo:storage-api"))
    implementation(project(":java-basic:spiDemo:storage-minio"))
    implementation(project(":java-basic:spiDemo:storage-rustfs"))
}