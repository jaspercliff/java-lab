plugins {
    id("java-library-convention")
    //其他模块可以通过 testImplementation(testFixtures(project(":base"))) 引用它提供的测试辅助代码
    id("java-test-fixtures")
}

dependencies{
    implementation("com.baomidou:mybatis-plus-annotation:3.5.17")
}
