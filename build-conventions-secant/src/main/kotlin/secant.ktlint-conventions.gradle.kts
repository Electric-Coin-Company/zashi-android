plugins {
    id("java")
}

val ktlint by configurations.creating

dependencies {
    ktlint("com.pinterest:ktlint:${project.property("KTLINT_VERSION")}") {
        attributes {
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named<Bundling>(Bundling.EXTERNAL))
        }
    }
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
        // Workaround for ktlint bug; force to run on an older JDK
        // https://github.com/pinterest/ktlint/issues/1274
        javaLauncher.set(javaToolchains.launcherFor {
            languageVersion.set(JavaLanguageVersion.of(JavaVersion.VERSION_11.majorVersion))
        })

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