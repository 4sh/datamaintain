import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

plugins {
    id("org.jetbrains.kotlin.jvm") version Versions.kotlin apply false
    id("com.palantir.graal") version "0.10.0" apply false
    id("com.palantir.git-version") version "0.12.3"
    id("com.adarshr.test-logger") version "3.1.0" apply false
    id("maven-publish")
}

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
}

subprojects {
    tasks.withType<KotlinJvmCompile>().all({
        kotlinOptions.jvmTarget = "1.8"
    })

    tasks.withType<Jar>().all {
        archiveBaseName.set("${rootProject.name}-${project.name}")

        manifest {
            attributes(mapOf(
                    "Implementation-Version" to project.version
            ))
        }
    }
}

task("pom") {
    generatePom()
}

publishing {
    repositories {
        maven {
            name = "OSSRH"
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
    }
}