package datamaintain.gradle

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.provider.Property
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.plugins.signing.SigningExtension

infix fun <T> Property<T>.by(value: T) {
    set(value)
}

const val keyIdEnvironmentVariableName = "SIGNING_KEY_ID"
const val signingKeyEnvironmentVariableName = "SIGNING_KEY"
const val signingKeyPassphraseEnvironmentVariableName = "SIGNING_KEY_PASSPHRASE"

fun MavenPublication.signPublicationIfKeyPresent(project: Project) {
    val keyId = System.getenv(keyIdEnvironmentVariableName)
    val signingKey = System.getenv(signingKeyEnvironmentVariableName)
    val signingKeyPassphrase = System.getenv(signingKeyPassphraseEnvironmentVariableName)

    if (!signingKey.isNullOrBlank()) {
        project.extensions.configure<SigningExtension>("signing") {
            useInMemoryPgpKeys(keyId, signingKey, signingKeyPassphrase)
            sign(this@signPublicationIfKeyPresent)
        }
    }
}

fun MavenPom.configureMavenCentralMetadata(project: Project) {
    name by project.name
    description by "One tool to maintain all your database schemas!"
    url by "https://github.com/4sh/datamaintain"

    licenses {
        license {
            name by "The Apache Software License, Version 2.0"
            url by "https://www.apache.org/licenses/LICENSE-2.0.txt"
            distribution by "repo"
        }
    }

    developers {
        developer {
            id by "4SH"
            name by "4SH"
            organization by "4SH"
            organizationUrl by "https://www.4sh.fr"
        }
    }

    scm {
        url by "https://github.com/4sh/datamaintain"
        connection by "scm:git:git://github.com/4sh/datamaintain.git"
        developerConnection by "scm:git:git@github.com:4sh/datamaintain.git"
    }
}

fun Project._publishing(configure: PublishingExtension.() -> Unit) {
    extensions.configure("publishing", configure)
}

fun Project._java(configure: JavaPluginExtension.() -> Unit) {
    extensions.configure("java", configure)
}