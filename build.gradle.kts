import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.70" apply false
    id("com.sourcemuse.mongo") version "1.0.7" apply false
    id("com.palantir.graal") version "0.4.0" apply false
    id("com.palantir.git-version") version "0.12.3"
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

    group = "com.github.4sh.datamaintain"
    version = computedVersion

    repositories {
        jcenter()
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