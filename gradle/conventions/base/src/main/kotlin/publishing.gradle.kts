package datamaintain.conventions

plugins {
    `maven-publish`
    `java-library`
    signing
}

java {
    withJavadocJar()
    withSourcesJar()
}

publishing {
    repositories {
        maven("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/") {
            name = "OSSRH"

            credentials {
                username = System.getenv("MAVEN_USERNAME")
                password = System.getenv("MAVEN_PASSWORD")
            }
        }
    }

    publications {
        register<MavenPublication>("ExposedJars") {
            artifactId = "${rootProject.name}-${project.name}"

            from(components["java"])

            pom {
                name.set(project.name)
                description.set("One tool to maintain all your database schemas!")
                url.set("https://github.com/4sh/datamaintain")

                licenses {
                    license {
                        name.set("The Apache Software License, Version 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
                    }
                }

                developers {
                    developer {
                        id.set("4SH")
                        name.set("4SH")
                        organization.set("4SH")
                        organizationUrl.set("https://www.4sh.fr")
                    }
                }

                scm {
                    url.set("https://github.com/4sh/datamaintain")
                    connection.set("scm:git:git://github.com/4sh/datamaintain.git")
                    developerConnection.set("scm:git:git@github.com:4sh/datamaintain.git")
                }
            }
        }
    }
}

signing {
    val signingKey = System.getenv("SIGNING_KEY")
        ?.takeUnless { it.isBlank() }
        ?: return@signing
    val signingKeyPassphrase = System.getenv("SIGNING_KEY_PASSPHRASE")
        ?.takeUnless { it.isBlank() }
        ?: return@signing

    useInMemoryPgpKeys(signingKey, signingKeyPassphrase)
    sign(publishing.publications)
}
