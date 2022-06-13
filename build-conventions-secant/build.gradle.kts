import org.jetbrains.kotlin.konan.properties.loadProperties

plugins {
    `kotlin-dsl`
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

dependencies {
    val rootProperties = getRootProperties()

    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${rootProperties.getProperty("KOTLIN_VERSION")}")
    implementation("com.android.tools.build:gradle:${rootProperties.getProperty("ANDROID_GRADLE_PLUGIN_VERSION")}")
    implementation("wtf.emulator:gradle-plugin:${rootProperties.getProperty("EMULATOR_WTF_GRADLE_PLUGIN_VERSION")}")
}

// A slightly gross way to use the root gradle.properties as the single source of truth for version numbers
fun getRootProperties() = loadProperties(File(project.projectDir.parentFile, "gradle.properties").path)