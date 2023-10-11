plugins {
    id("datamaintain.conventions.kotlin")

    alias(libs.plugins.testLogger)

    `maven-publish` // Needed for Jitpack
}

tasks.getByPath("test").doFirst({
    with<org.gradle.api.tasks.testing.Test, kotlin.Unit>(this as Test) {
        this.useJUnitPlatform()
    }
})

kotlin {
    jvmToolchain(17)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = project.group.toString()
            artifactId = "datamaintain-" + project.name
            version = project.version.toString()

            from(components["java"])
        }
    }
}

repositories {
    mavenCentral()
}

dependencies {
    api(platform(libs.kotlin.bom))
    implementation(libs.kotlin.jdk8)

    implementation(libs.kotlinLogging)

    testImplementation(libs.junit.jupiter.api)
    testImplementation(libs.junit.jupiter.engine)
    testRuntimeOnly(libs.junit.jupiter.engine)
    testImplementation(libs.strikt)
    testImplementation(libs.mockk)
    testImplementation(libs.testContainers)
    testImplementation(libs.testContainers.jupiter)
    testImplementation(libs.testContainers.mongodb)

    compileOnly(projects.modules.core)
    testImplementation(projects.modules.core)
    testImplementation(libs.h2database)
}
