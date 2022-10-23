import org.gradle.wrapper.Install
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

plugins {
    id("org.jetbrains.kotlin.jvm") version Versions.kotlin apply false
    id("com.palantir.graal") version "0.10.0" apply false
    id("com.palantir.git-version") version "0.12.3"
    id("com.adarshr.test-logger") version "3.1.0" apply false
    id("maven-publish")
    signing
}

val modulesToPublish = listOf(
    "core",
    "driver-jdbc",
    "driver-mongo",
    "driver-mongo-mapping-serialization",
    "driver-mongo-mapping-jackson",
    "driver-mongo-mapping-gson",
    "driver-mongo-mapping-test"
)

allprojects {
    apply<com.palantir.gradle.gitversion.GitVersionPlugin>()

    val versionDetails: groovy.lang.Closure<com.palantir.gradle.gitversion.VersionDetails> by extra
    val lastTag = versionDetails().lastTag

    val computedVersion: String = if (lastTag != "") {
        lastTag
    } else {
        "SNAPSHOT"
    }

    group = "io.github.4sh.datamaintain"
    version = computedVersion

    repositories {
        mavenCentral()
    }

    if (modulesToPublish.contains(this.name)) {
        apply(from = rootProject.file("buildScripts/gradle/publishing.gradle.kts"))
    }
}


configure(subprojects) {
    tasks.withType<KotlinJvmCompile>().all {
        kotlinOptions.jvmTarget = "1.8"
    }
}
