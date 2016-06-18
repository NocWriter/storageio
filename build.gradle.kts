plugins {
    id("java")
}

group = "io.storage"
version = "storage"
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

tasks.withType<Test> {
    useJUnitPlatform()
}
