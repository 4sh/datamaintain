import org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.internal.logging.text.StyledTextOutput
import org.gradle.internal.logging.text.StyledTextOutputFactory
import org.gradle.kotlin.dsl.support.serviceOf
import java.util.*

plugins {
    id("org.jetbrains.kotlin.jvm") version Versions.kotlin apply false
    id("com.palantir.graal") version "0.10.0" apply false
    id("com.palantir.git-version") version "0.12.3"
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

    tasks.withType<Test>().all {
        testLogging {
            // set options for log level LIFECYCLE
            events = setOf(
                TestLogEvent.FAILED,
                TestLogEvent.PASSED,
                TestLogEvent.SKIPPED,
                TestLogEvent.STANDARD_OUT
            )
            exceptionFormat = TestExceptionFormat.FULL
            showExceptions = true
            showCauses = true
            showStackTraces = true

            // set options for log level DEBUG and INFO
            debug {
                events = setOf(
                    TestLogEvent.STARTED,
                    TestLogEvent.FAILED,
                    TestLogEvent.PASSED,
                    TestLogEvent.SKIPPED,
                    TestLogEvent.STANDARD_ERROR,
                    TestLogEvent.STANDARD_OUT
                )
                exceptionFormat = TestExceptionFormat.FULL
            }
            info.events = debug.events
            info.exceptionFormat = debug.exceptionFormat

            addTestListener(object : TestListener {
                val failedTests = mutableListOf<TestDescriptor>()
                val skippedTests = mutableListOf<TestDescriptor>()

                override fun beforeSuite(suite: TestDescriptor) {}
                override fun beforeTest(testDescriptor: TestDescriptor) {}

                /**
                 * Add test to list if failed or skipped
                 */
                override fun afterTest(testDescriptor: TestDescriptor, result: TestResult) {
                    when (result.resultType) {
                        TestResult.ResultType.FAILURE -> failedTests.add(testDescriptor)
                        TestResult.ResultType.SKIPPED -> skippedTests.add(testDescriptor)
                        else -> Unit
                    }
                }

                /**
                 * Print test suite summary
                 */
                override fun afterSuite(suite: TestDescriptor, result: TestResult) {
                    if (suite.parent != null) {
                        return
                    }

                    val summaryStyledTextOutput = serviceOf<StyledTextOutputFactory>().create("test-summary")

                    val output = "Test results: ${result.resultType} (${result.testCount} tests, " +
                            "${result.successfulTestCount} passed, " +
                            "${result.failedTestCount} failed, " +
                            "${result.skippedTestCount} skipped)"

                    summaryStyledTextOutput.println()

                    val style = when (result.resultType) {
                        TestResult.ResultType.SUCCESS -> StyledTextOutput.Style.SuccessHeader
                        TestResult.ResultType.SKIPPED -> StyledTextOutput.Style.SuccessHeader
                        TestResult.ResultType.FAILURE -> StyledTextOutput.Style.FailureHeader
                        else -> StyledTextOutput.Style.Error
                    }
                    summaryStyledTextOutput.style(style).println(output)

                    failedTests.takeIf { it.isNotEmpty() }?.summary("FAILED", summaryStyledTextOutput.style(StyledTextOutput.Style.Failure))
                    summaryStyledTextOutput.println()
                    skippedTests.takeIf { it.isNotEmpty() }?.summary("SKIPPED", summaryStyledTextOutput.style(StyledTextOutput.Style.Info))
                }

                fun List<TestDescriptor>.summary(
                    subject: String,
                    subjectStyledTextOutput: StyledTextOutput,
                    prefix: String = "  "
                ) {
                    subjectStyledTextOutput.println(subject)
                    forEach { test -> subjectStyledTextOutput.println(prefix + test.computeFullName()) }
                }

                fun TestDescriptor.computeFullName(separator: String = " > "): String = this.computePath()
                        .dropWhile { it.className == null }
                        .map { it.displayName }
                        .joinToString(separator)

                fun TestDescriptor.computePath(): Sequence<TestDescriptor> {
                    val path = LinkedList<TestDescriptor>()
                    path.addFirst(this)

                    var testDescriptorParent = parent
                    while (testDescriptorParent != null) {
                        path.addFirst(testDescriptorParent)
                        testDescriptorParent = testDescriptorParent.parent
                    }

                    return path.asSequence()
                }
            })
        }
    }
}
