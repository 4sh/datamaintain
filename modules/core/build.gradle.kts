
plugins {
    id("org.jetbrains.kotlin.jvm")
    `maven-publish` // Needed for Jitpack
    id("com.adarshr.test-logger")
}

repositories {
    mavenCentral()
}

baseProject()

dependencies {
    "testImplementation"("ch.qos.logback:logback-classic:${Versions.logbackClassic}")
}

