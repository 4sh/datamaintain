import org.gradle.api.Project
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.dependencies

fun Project.kotlinProject() {
    dependencies {
        "implementation"(platform("org.jetbrains.kotlin:kotlin-bom"))
        "implementation"("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    }
}

fun Project.baseProject() {
    kotlinProject()

    dependencies {
        "implementation"("io.github.microutils:kotlin-logging:${Versions.kotlinLogging}")
       // TODO MRU a déplacer dans cli
        "implementation"("ch.qos.logback:logback-classic:${Versions.logbackClassic}")

        "testImplementation"("org.junit.jupiter:junit-jupiter-api:${Versions.junit}")
        "testImplementation"("org.junit.jupiter:junit-jupiter-params:${ Versions.junit }")
        "testImplementation"("io.strikt:strikt-core:${ Versions.strikt }")
        "testImplementation"("io.mockk:mockk:${ Versions.mockk }")

        "testRuntimeOnly"("org.junit.jupiter:junit-jupiter-engine:${ Versions.junit }")
    }

    tasks.getByPath("test").doFirst({
        with(this as Test) {
            useJUnitPlatform()
        }
    })

}