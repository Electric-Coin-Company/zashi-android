import org.jetbrains.kotlin.konan.properties.loadProperties

plugins {
    `kotlin-dsl`
}


dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin")
    implementation("com.android.tools.build:gradle:${getAndroidGradlePluginVersion()}")
}

fun getAndroidGradlePluginVersion(): String {
    // A slightly gross way to use the root gradle.properties as the single source of truth for version numbers
    val properties = run {
        val rootPropertiesFile = File(project.projectDir.parentFile, "gradle.properties")
        loadProperties(rootPropertiesFile.path)
    }

    return properties.getProperty("ANDROID_GRADLE_PLUGIN_VERSION")
}
