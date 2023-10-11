plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.palantir.graal) apply false
    alias(libs.plugins.palantir.git)
    alias(libs.plugins.testLogger) apply false
}
