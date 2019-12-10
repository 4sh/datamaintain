import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.50" apply false
    id("com.sourcemuse.mongo") version "1.0.7" apply false
    id("com.palantir.graal") version "0.4.0" apply false
}

allprojects {
    group = "datamaintain"
    version = "0.0"

    repositories {
        jcenter()
    }
}

subprojects {
    tasks.withType<KotlinJvmCompile>().all({
        kotlinOptions.jvmTarget = "1.8"
    })

    tasks.withType<Jar>().all {
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
