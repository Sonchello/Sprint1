val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String = "1.4.12"

val postgres_version: String by project
val h2_version: String = "2.2.220"
val exposed_version: String by project
plugins {
    kotlin("jvm") version "1.9.21"
    application
    id("io.ktor.plugin") version "2.3.7"
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.21"
}

group = "com.protei1sprint"
version = "0.0.1"

application {
    mainClass.set("com.protei1sprint.ApplicationKt")

    val isDevelopment: Boolean = project.ext.has("development")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-client-core:2.3.7")
    implementation("io.ktor:ktor-client-apache:2.3.7")
    implementation("io.ktor:ktor-client-json:2.3.7")
    implementation("io.ktor:ktor-server-auth:2.3.7")

    implementation("io.ktor:ktor-server-core-jvm:2.3.7")
    implementation("io.ktor:ktor-server-content-negotiation-jvm:2.3.7")
    implementation("io.ktor:ktor-serialization-kotlinx-json-jvm:2.3.7")
    implementation("org.postgresql:postgresql:$postgres_version")
    implementation("com.h2database:h2:2.2.220")
    implementation("org.jetbrains.exposed:exposed-core:$exposed_version")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposed_version")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.7")
    implementation("ch.qos.logback:logback-classic:1.4.12")
    testImplementation("io.ktor:ktor-server-tests-jvm:2.3.7")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}
