pluginManager.withPlugin("org.jetbrains.kotlin.multiplatform") {
    extensions.findByType<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension>()?.apply {
        jvmToolchain(project.property("JVM_TOOLCHAIN").toString().toInt())

        targets.all {
            compilations.all {
                compilerOptions.options.allWarningsAsErrors.set(
                    project.property("ZCASH_IS_TREAT_WARNINGS_AS_ERRORS").toString().toBoolean()
                )

                compilerOptions.options.freeCompilerArgs.addAll("-opt-in=kotlin.RequiresOptIn")
            }
        }
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(project.property("KOTLIN_JVM_TARGET").toString().toInt())
}
