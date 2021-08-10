
plugins {
    id("org.jetbrains.kotlin.jvm")
    maven // Needed for Jitpack
}

repositories {
    jcenter()
}

baseProject()

dependencies {
    "testImplementation"("ch.qos.logback:logback-classic:${Versions.logbackClassic}")
}

