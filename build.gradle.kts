object Versions {
    val junit = "5.5.2"
    val strikt = "0.21.1"
}

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.50"
    application
    maven
}

repositories {
    jcenter()
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    testImplementation("org.junit.jupiter:junit-jupiter-api:${Versions.junit}")
    testImplementation("org.junit.jupiter:junit-jupiter-params:${Versions.junit}")
    testImplementation("io.strikt:strikt-core:${Versions.strikt}")

    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${Versions.junit}")
}

application {
    mainClassName = "datamaintain.AppKt"
}
