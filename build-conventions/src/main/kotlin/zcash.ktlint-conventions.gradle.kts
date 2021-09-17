plugins {
    id("java")
}

val ktlint by configurations.creating

dependencies {
    ktlint("com.pinterest:ktlint:${project.property("KTLINT_VERSION")}")
}

tasks {
    val editorConfigFile = rootProject.file(".editorconfig")
    val ktlintArgs = listOf("**/src/**/*.kt", "!**/build/**.kt", "--editorconfig=$editorConfigFile")

    register("ktlint", org.gradle.api.tasks.JavaExec::class) {
        description = "Check code style with ktlint"
        classpath = ktlint
        mainClass.set("com.pinterest.ktlint.Main")
        args = ktlintArgs
    }

    register("ktlintFormat", org.gradle.api.tasks.JavaExec::class) {
        description = "Apply code style formatting with ktlint"
        classpath = ktlint
        mainClass.set("com.pinterest.ktlint.Main")
        args = listOf("-F") + ktlintArgs
    }
}

java {
    val javaVersion = JavaVersion.toVersion(project.property("ANDROID_JVM_TARGET").toString())
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
}