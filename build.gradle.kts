import kotlinx.kover.api.KoverMergedConfig

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

plugins {
    id("com.github.ben-manes.versions")
    id("com.osacky.fulladle")
    id("io.gitlab.arturbosch.detekt")
    id("org.jetbrains.kotlinx.kover")
    id("secant.ktlint-conventions")
    id("secant.rosetta-conventions")
}

val uiIntegrationModuleName: String = projects.uiIntegrationTest.name
val uiScreenshotModuleName: String = projects.uiScreenshotTest.name

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
        // To exclude the whole pure test modules
        exclude(uiIntegrationModuleName, uiScreenshotModuleName)
        // To regenerate the config, run the task `detektGenerateConfig`
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

fladle {
    // Firebase Test Lab has min and max values that might differ from our project's
    // These are determined by `gcloud firebase test android models list`
    @Suppress("MagicNumber", "PropertyName", "VariableNaming")
    val FIREBASE_TEST_LAB_MIN_SDK = 23

    @Suppress("MagicNumber", "PropertyName", "VariableNaming")
    val FIREBASE_TEST_LAB_MAX_SDK = 33

    val minSdkVersion = run {
        // Fladle will use the app APK as the additional APK, so we have to
        // use the app's minSdkVersion here.
        val buildMinSdk = project.properties["ANDROID_APP_MIN_SDK_VERSION"].toString().toInt()
        buildMinSdk.coerceAtLeast(FIREBASE_TEST_LAB_MIN_SDK).toString()
    }
    val targetSdkVersion = run {
        val buildTargetSdk = project.properties["ANDROID_TARGET_SDK_VERSION"].toString().toInt()
        buildTargetSdk.coerceAtMost(FIREBASE_TEST_LAB_MAX_SDK).toString()
    }

    val firebaseTestLabKeyPath = project.properties["ZCASH_FIREBASE_TEST_LAB_API_KEY_PATH"].toString()
    val firebaseProject = project.properties["ZCASH_FIREBASE_TEST_LAB_PROJECT"].toString()

    if (firebaseTestLabKeyPath.isNotEmpty()) {
        serviceAccountCredentials.set(File(firebaseTestLabKeyPath))
    } else if (firebaseProject.isNotEmpty()) {
        projectId.set(firebaseProject)
    }

    devices.addAll(
        mapOf("model" to "Pixel2", "version" to minSdkVersion),
        mapOf("model" to "Pixel2.arm", "version" to targetSdkVersion)
    )

    @Suppress("MagicNumber")
    flakyTestAttempts.set(2)

    // Always use orchestrator for Firebase Test Lab.
    // Some submodules don't need it, but it is difficult to configure on a per-module basis from here
    // since this configuration applies to all modules.
    useOrchestrator.set(true)
    environmentVariables.set(mapOf("clearPackageData" to "true"))

    flankVersion.set(libs.versions.flank.get())

    filesToDownload.set(listOf(
        "*/matrix_*/*test_results_merged\\.xml",
        "*/matrix_*/*/artifacts/sdcard/googletest/test_outputfiles/*\\.png"
    ))

    directoriesToPull.set(listOf(
        "/sdcard/googletest/test_outputfiles"
    ))
}

kover {
    isDisabled.set(!project.property("IS_KOTLIN_TEST_COVERAGE_ENABLED").toString().toBoolean())
    engine.set(kotlinx.kover.api.JacocoEngine(project.property("JACOCO_VERSION").toString()))

    // Don't run on the Android projects, as they have coverage generated in a different way
    // through Android's instrumented tests
    extensions.configure<KoverMergedConfig> {
        enable()
        filters {
            projects {
                excludes.addAll(setOf(
                    "app",
                    "crash-android-lib",
                    "preference-impl-android-lib",
                    "sdk-ext-lib",
                    "sdk-ext-ui-lib",
                    "spackle-android-lib",
                    "test-lib",
                    "ui-benchmark-test",
                    "ui-design-lib",
                    "ui-integration-test",
                    "ui-lib",
                    "ui-screenshot-test"
                ))
            }
        }
    }
}
