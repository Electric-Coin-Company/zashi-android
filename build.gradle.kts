buildscript {
    dependencyLocking {
        lockMode.set(LockMode.STRICT)
        lockAllConfigurations()
    }

    repositories {
        val isRepoRestrictionEnabled = true

        maven("https://dl.google.com/dl/android/maven2/") { //google()
            if (isRepoRestrictionEnabled) {
                content {
                    includeGroup("androidx.navigation")
                    includeGroup("com.android.tools")
                    includeGroup("com.google.testing.platform")
                    includeGroupByRegex("androidx.*")
                    includeGroupByRegex("com\\.android.*")
                    includeGroupByRegex("com\\.android\\.tools.*")
                }
            }
        }
        maven("https://plugins.gradle.org/m2/") { // gradlePluginPortal()
            if (isRepoRestrictionEnabled) {
                content {
                    excludeGroup("androidx.navigation")
                    excludeGroup("com.android.tools")
                    excludeGroup("com.google.testing.platform")
                    excludeGroupByRegex("androidx.*")
                    excludeGroupByRegex("com\\.android.*")
                    excludeGroupByRegex("com\\.android\\.tools.*")
                }
            }
        }
        maven("https://repo.maven.apache.org/maven2/") { // mavenCentral()
            if (isRepoRestrictionEnabled) {
                content {
                    excludeGroup("androidx.navigation")
                    excludeGroup("com.android.tools")
                    excludeGroup("com.google.testing.platform")
                    excludeGroupByRegex("androidx.*")
                    excludeGroupByRegex("com\\.android.*")
                    excludeGroupByRegex("com\\.android\\.tools.*")
                }
            }
        }
    }
    dependencies {
        classpath("com.android.tools.build:gradle:${properties["ANDROID_GRADLE_PLUGIN_VERSION"]}")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:${properties["ANDROIDX_NAVIGATION_VERSION"]}")
    }
}

plugins {
    id("com.github.ben-manes.versions")
    id("io.gitlab.arturbosch.detekt")
    id("zcash.ktlint-conventions")
}

tasks {
    register("detektAll", io.gitlab.arturbosch.detekt.Detekt::class) {
        parallel = true
        setSource(files(projectDir))
        include("**/*.kt")
        include("**/*.kts")
        exclude("**/resources/**")
        exclude("**/build/**")
        exclude("**/commonTest/**")
        exclude("**/jvmTest/**")
        exclude("**/androidTest/**")
        config.setFrom(files("${rootProject.projectDir}/tools/detekt.yml"))
        buildUponDefaultConfig = true
    }

    withType<com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask> {
        gradleReleaseChannel = "current"

        resolutionStrategy {
            componentSelection {
                all {
                    if (isNonStable(candidate.version) && !isNonStable(currentVersion)) {
                        reject("Unstable")
                    }
                }
            }
        }
    }
}

val unstableKeywords = listOf("alpha", "beta", "rc", "m", "ea", "build")

fun isNonStable(version: String): Boolean {
    val versionLowerCase = version.toLowerCase()

    return unstableKeywords.any { versionLowerCase.contains(it) }
}


// All of this should be refactored to build-conventions
subprojects {
    pluginManager.withPlugin("com.android.library") {
        project.the<com.android.build.gradle.LibraryExtension>().apply {
            configureBaseExtension()

            // TODO [#5]: Figure out how to move this into the build-conventions
            testCoverage {
                jacocoVersion = libs.versions.jacoco.get()
            }
        }
    }

    pluginManager.withPlugin("com.android.application") {
        project.the<com.android.build.gradle.AppExtension>().apply {
            configureBaseExtension()
        }
    }
}

fun com.android.build.gradle.BaseExtension.configureBaseExtension() {
    // TODO [#22]: Figure out how to move this into build-conventions
    testOptions {
        animationsDisabled = true

        if (project.property("IS_USE_TEST_ORCHESTRATOR").toString().toBoolean()) {
            execution = "ANDROIDX_TEST_ORCHESTRATOR"
        }
    }
}