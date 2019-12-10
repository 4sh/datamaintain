import org.gradle.api.Task
import java.io.File


fun Task.generatePom() {
    // generates a pom.xml for snyk.io testing - use with `./gradlew pom && snyk test`
    doLast {
        val sb = StringBuilder()
        sb.append("""
        <project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
          xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
          <modelVersion>4.0.0</modelVersion>
          <groupId>${project.group}</groupId>
          <artifactId>${project.name}</artifactId>
          <version>${project.version}</version>
          <repositories>
                <repository>
                  <id>jcenter</id>
                  <url>https://jcenter.bintray.com</url>
                </repository>
                <repository>
                    <id>jitpack.io</id>
                    <url>https://jitpack.io</url>
                </repository>
          </repositories>
          <dependencies>

        """.trimIndent())

        sb.append(project.subprojects
                .flatMap { it.configurations }
                .filter { !it.name.startsWith("test") }
                .flatMap { it.dependencies }
                .filter { !(it.group?:"").startsWith("com.beclm") }
                .filter { it.version != null }
                .map { "    <dependency><groupId>${it.group}</groupId><artifactId>${it.name}</artifactId><version>${it.version}</version></dependency>" }
                .toSet()
                .joinToString(separator = "\n"))

        sb.append("""
          </dependencies>
          </project>
        """.trimIndent())

        File("pom.xml").writeText(sb.toString())
    }
}


