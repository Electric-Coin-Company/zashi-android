buildscript {
    dependencyLocking {
        lockMode.set(LockMode.STRICT)
        lockAllConfigurations()
    }
}

plugins {
    id("com.github.ben-manes.versions")
    id("com.osacky.fulladle")
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

// Firebase Test Lab has min and max values that might differ from our project's
// These are determined by `gcloud firebase test android models list`
@Suppress("MagicNumber", "PropertyName", "VariableNaming")
val FIREBASE_TEST_LAB_MIN_API = 23
@Suppress("MagicNumber", "PropertyName", "VariableNaming")
val FIREBASE_TEST_LAB_MAX_API = 30

val firebaseTestLabKeyPath = project.properties["ZCASH_FIREBASE_TEST_LAB_API_KEY_PATH"].toString()
if (firebaseTestLabKeyPath.isNotBlank()) {
    val minSdkVersion = run {
        val buildMinSdk = project.properties["ANDROID_MIN_SDK_VERSION"].toString().toInt()
        buildMinSdk.coerceAtLeast(FIREBASE_TEST_LAB_MIN_API).toString()
    }
    val targetSdkVersion = run {
        val buildTargetSdk = project.properties["ANDROID_TARGET_SDK_VERSION"].toString().toInt()
        buildTargetSdk.coerceAtMost(FIREBASE_TEST_LAB_MAX_API).toString()
    }
    fladle {
        serviceAccountCredentials.set(File(firebaseTestLabKeyPath))
        // TODO [#282]: Replace this with NexusLowRes once tests pass on larger screen sizes
        devices.addAll(
            mapOf("model" to "Nexus6", "version" to minSdkVersion),
            mapOf("model" to "Pixel2", "version" to targetSdkVersion)
        )

        @Suppress("MagicNumber")
        flakyTestAttempts.set(2)

        if (project.properties["IS_USE_TEST_ORCHESTRATOR"].toString().toBoolean()) {
            useOrchestrator.set(true)
            environmentVariables.set(mapOf("clearPackageData" to "true"))
        } else {
            useOrchestrator.set(false)
        }

        flankVersion.set(libs.versions.flank.get())

        filesToDownload.set(listOf(
            "*/matrix_*/*test_results_merged\\.xml"
        ))
    }
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
