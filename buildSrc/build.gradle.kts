import org.jetbrains.kotlin.konan.properties.loadProperties

plugins {
    `kotlin-dsl-base`
}

buildscript {
    dependencyLocking {
        // This property is treated specially, as it is not defined by default in the root gradle.properties
        // and declaring it in the root gradle.properties is ignored by included builds. This only picks up
        // a value declared as a system property, a command line argument, or a an environment variable.
        val isDependencyLockingEnabled = if (project.hasProperty("ZCASH_IS_DEPENDENCY_LOCKING_ENABLED")) {
            project.property("ZCASH_IS_DEPENDENCY_LOCKING_ENABLED").toString().toBoolean()
        } else {
            true
        }

        if (isDependencyLockingEnabled) {
            lockAllConfigurations()
        }
    }
}


dependencyLocking {
    // This property is treated specially, as it is not defined by default in the root gradle.properties
    // and declaring it in the root gradle.properties is ignored by included builds. This only picks up
    // a value declared as a system property, a command line argument, or a an environment variable.
    val isDependencyLockingEnabled = if (project.hasProperty("ZCASH_IS_DEPENDENCY_LOCKING_ENABLED")) {
        project.property("ZCASH_IS_DEPENDENCY_LOCKING_ENABLED").toString().toBoolean()
    } else {
        true
    }

    if (isDependencyLockingEnabled) {
        lockAllConfigurations()
    }
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