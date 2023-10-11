plugins {
    id("org.jetbrains.kotlin.jvm")
    `maven-publish` // Needed for Jitpack
    id("com.adarshr.test-logger")
}

repositories {
    mavenCentral()
}

dependencies {
    "api"((this.platform("org.jetbrains.kotlin:kotlin-bom:${Versions.kotlin}")))
    "implementation"("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

dependencies {
    "implementation"("io.github.microutils:kotlin-logging:${Versions.kotlinLogging}")

    "testImplementation"("org.junit.jupiter:junit-jupiter-api:${Versions.junit}")
    "testImplementation"("org.junit.jupiter:junit-jupiter-params:${Versions.junit}")
    "testRuntimeOnly"("org.junit.jupiter:junit-jupiter-engine:${Versions.junit}")
    "testImplementation"("io.strikt:strikt-core:${Versions.strikt}")
    "testImplementation"("io.mockk:mockk:${Versions.mockk}")
    "testImplementation"("org.testcontainers:testcontainers:${Versions.testcontainers}")
    "testImplementation"("org.testcontainers:junit-jupiter:${Versions.testcontainers}")
    "testImplementation"("org.testcontainers:mongodb:${Versions.testcontainers}")
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

dependencies {
    "testImplementation"("ch.qos.logback:logback-classic:${Versions.logbackClassic}")
}
