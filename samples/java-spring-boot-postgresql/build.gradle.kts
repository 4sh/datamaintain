plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.springframework.boot") version("2.7.8")
}

kotlin {
    jvmToolchain(17)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":modules:core"))
    implementation(project(":modules:driver-jdbc"))
    implementation("org.postgresql:postgresql:42.7.2")

    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.2.2"))
    implementation("org.springframework.boot:spring-boot-starter")
}
