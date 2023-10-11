plugins {
    `kotlin-dsl`
}

group = "datamaintain.conventions"

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.gradle.kotlin.jvm)
    implementation(libs.gradle.palantir.git)

    // Access the version catalog inside the plugin
    // See https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}
