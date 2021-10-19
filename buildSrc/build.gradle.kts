import org.jetbrains.kotlin.konan.properties.loadProperties

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
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