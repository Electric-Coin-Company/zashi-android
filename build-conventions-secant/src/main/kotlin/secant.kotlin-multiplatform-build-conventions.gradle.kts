import org.gradle.jvm.toolchain.JavaToolchainSpec

pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
    extensions.findByType<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension>()?.apply {
        jvmToolchain {
            val javaVersion = JavaVersion.toVersion(project.property("JVM_TOOLCHAIN").toString())
            val javaLanguageVersion = JavaLanguageVersion.of(javaVersion.majorVersion)
            languageVersion.set(javaLanguageVersion)
        }

        targets.matching { it.platformType.name == "jvm" }.all {
            (this as org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget).apply {
                val javaTargetVersion = project.property("ANDROID_JVM_TARGET").toString()

                compilations.all {
                    kotlinOptions {
                        jvmTarget = javaTargetVersion
                    }
                }
            }
        }

        targets.all {
            compilations.all {
                kotlinOptions {
                    allWarningsAsErrors = project.property("ZCASH_IS_TREAT_WARNINGS_AS_ERRORS").toString().toBoolean()
                    freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
                }
            }
        }
    }
}
