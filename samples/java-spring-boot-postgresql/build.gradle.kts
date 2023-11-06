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
    implementation("org.postgresql:postgresql:42.1.4")

    implementation(platform("org.springframework.boot:spring-boot-dependencies:3.1.5"))
    implementation("org.springframework.boot:spring-boot-starter")
}
