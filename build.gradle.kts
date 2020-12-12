import org.gradle.internal.logging.text.StyledTextOutput
import org.gradle.kotlin.dsl.support.serviceOf

plugins {
    id("java")
    id("maven-publish")
}

/**
 * Maintains artifact's properties (group, name, version).
 */
object Artifact {
    const val GROUP_ID = "io.storage"
    const val NAME = "core"
    const val VERSION = "1.0.0"
}

/**
 * Maintains the list of properties required for authorization.
 */
object AuthProperties {
    const val USERNAME_PROPERTY_NAME = "GITHUB_ACTOR"
    const val PASSWORD_PROPERTY_NAME = "GITHUB_TOKEN"
}

// Logger helper for styling output (e.g.: writing message in bold/color).
val styledOutput: StyledTextOutput = project
        .serviceOf<org.gradle.internal.logging.text.StyledTextOutputFactory>()
        .create("styledOutput")

//
// Artifact properties.
//
group = Artifact.GROUP_ID
version = Artifact.VERSION

//
// Java compiler options.
//
java.sourceCompatibility = JavaVersion.VERSION_11

repositories {
    mavenCentral()
}

dependencies {
    //
    // Logging.
    //
    implementation("org.slf4j:slf4j-api:1.7.30")

    // We only actually logs something during unit tests.
    testImplementation("org.apache.logging.log4j:log4j-slf4j-impl:2.14.0")
    testImplementation("org.apache.logging.log4j:log4j-api:2.14.0")
    testImplementation("org.apache.logging.log4j:log4j-core:2.14.0")

    // JSON.
    implementation("com.fasterxml.jackson.core:jackson-databind:2.7.4")

    // Google JIM file system.
    implementation("com.google.jimfs:jimfs:1.1")

    // Dropbox
    implementation("com.dropbox.core:dropbox-core-sdk:2.0.6")

    //
    // Unit testing.
    //
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
    testImplementation("org.assertj:assertj-core:3.18.1")
    testImplementation("org.mockito:mockito-core:3.6.28")


}

tasks {
    test {
        useJUnitPlatform()
    }

    //
    // Before running publishing procedure, make sure we have everything we need.
    //
    publish {
        if (!hasVariable(AuthProperties.USERNAME_PROPERTY_NAME) || !hasVariable(AuthProperties.PASSWORD_PROPERTY_NAME)) {
            styledOutput
                    .style(StyledTextOutput.Style.FailureHeader)
                    .println("Error: Missing either '${AuthProperties.USERNAME_PROPERTY_NAME}' or " +
                            "'${AuthProperties.PASSWORD_PROPERTY_NAME}' system properties required for " +
                            "publishing an artifact.")

            throw GradleException("Missing authentication properties.")
        }
    }
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/NocWriter/storageio")
            credentials {
                username = getVariable(AuthProperties.USERNAME_PROPERTY_NAME)
                password = getVariable(AuthProperties.PASSWORD_PROPERTY_NAME)
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            groupId = Artifact.GROUP_ID
            artifactId = Artifact.NAME
            version = Artifact.VERSION

            from(components["java"])
        }
    }
}

/**
 * Fetch property value from Java system properties (first priority) or environment variables (second priority).
 *
 * @param name Name of property.
 * @return Property value or {@code null} if property is undefined.
 */
fun getVariable(name: String): String? {
    return System.getProperty(name, null) ?: System.getenv(name)
}

/**
 * Check if a system property/environment variable is defined.
 *
 * @param name Name of property.
 * @return {@code true} if property is defined, {@code false} if not.
 */
fun hasVariable(name: String): Boolean {
    return getVariable(name) != null
}
