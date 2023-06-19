import org.jetbrains.kotlin.konan.properties.loadProperties

plugins {
    `kotlin-dsl`
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

dependencies {
    val rootProperties = getRootProperties()

    implementation("com.android.tools.build:gradle:${rootProperties.getProperty("ANDROID_GRADLE_PLUGIN_VERSION")}")
    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:${rootProperties.getProperty("DETEKT_VERSION")}")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${rootProperties.getProperty("KOTLIN_VERSION")}")
    implementation("wtf.emulator:gradle-plugin:${rootProperties.getProperty("EMULATOR_WTF_GRADLE_PLUGIN_VERSION")}")
    implementation(
        "org.jetbrains.kotlinx.kover:org.jetbrains.kotlinx.kover.gradle.plugin:" +
            rootProperties.getProperty("KOVER_VERSION")
    )
}

// A slightly gross way to use the root gradle.properties as the single source of truth for version numbers
fun getRootProperties() = loadProperties(File(project.projectDir.parentFile, "gradle.properties").path)