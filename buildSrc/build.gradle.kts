import org.jetbrains.kotlin.konan.properties.loadProperties

plugins {
    `kotlin-dsl-base`
}

buildscript {
    dependencyLocking {
        lockAllConfigurations()
    }
}

dependencyLocking {
    lockAllConfigurations()
}

// Per conversation in the KotlinLang Slack, Gradle uses Java 8 compatibility internally
// for all build scripts.
// https://kotlinlang.slack.com/archives/C19FD9681/p1636632870122900?thread_ts=1636572288.117000&cid=C19FD9681
java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
}

dependencies {
    val rootProperties = getRootProperties()

    implementation("org.eclipse.jgit:org.eclipse.jgit:${rootProperties.getProperty("JGIT_VERSION")}")
}

// A slightly gross way to use the root gradle.properties as the single source of truth for version numbers
fun getRootProperties() = loadProperties(File(project.projectDir.parentFile, "gradle.properties").path)