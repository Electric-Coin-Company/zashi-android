plugins {
    id("com.android.test")
    kotlin("android")
    id("secant.android-build-conventions")
    id("com.osacky.fladle")
    id("wtf.emulator.gradle")
    id("secant.emulator-wtf-conventions")
}

// Force orchestrator to be used for this module, because we need cleared state to generate screenshots
val isOrchestratorEnabled = true

android {
    namespace = "co.electriccoin.zcash.ui.integration"
    // Target needs to be set to com.android.application type module
    targetProjectPath = ":${projects.app.name}"
    // Run tests in this module
    experimentalProperties["android.experimental.self-instrumenting"] = true

    defaultConfig {
        if (isOrchestratorEnabled) {
            testInstrumentationRunnerArguments["clearPackageData"] = "true"
        }

        testInstrumentationRunner = "co.electriccoin.zcash.test.ZcashUiTestRunner"
    }

    // Define the same flavors as in app module
    flavorDimensions.add("network")
    productFlavors {
        create("zcashtestnet") {
            dimension = "network"
        }
        create("zcashmainnet") {
            dimension = "network"
        }
    }

    if (isOrchestratorEnabled) {
        testOptions {
            execution = "ANDROIDX_TEST_ORCHESTRATOR"
        }
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.androidx.compose.compiler.get().versionConstraint.displayName
    }
}

dependencies {
    implementation(projects.uiLib)
    implementation(projects.uiDesignLib)
    implementation(projects.testLib)
    implementation(projects.spackleAndroidLib)

    implementation(libs.bundles.androidx.test)
    implementation(libs.bundles.androidx.compose.core)
    implementation(libs.bundles.play.core)

    implementation(libs.androidx.compose.test.junit)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.uiAutomator)

    if (isOrchestratorEnabled) {
        androidTestUtil(libs.androidx.test.orchestrator) {
            artifact {
                type = "apk"
            }
        }
    }
}

emulatorwtf {
    directoriesToPull.set(listOf("/sdcard/googletest/test_outputfiles"))

    val appMinSdkVersion = run {
        @Suppress("MagicNumber", "PropertyName", "VariableNaming")
        val EMULATOR_WTF_MIN_SDK = 23

        val buildMinSdk = project.properties["ANDROID_APP_MIN_SDK_VERSION"].toString().toInt()
        buildMinSdk.coerceAtLeast(EMULATOR_WTF_MIN_SDK).toString()
    }

    val targetSdkVersion = run {
        @Suppress("MagicNumber", "PropertyName", "VariableNaming")
        val EMULATOR_WTF_MAX_SDK = 31

        val buildTargetSdk = project.properties["ANDROID_TARGET_SDK_VERSION"].toString().toInt()
        buildTargetSdk.coerceAtMost(EMULATOR_WTF_MAX_SDK).toString()
    }

    devices.set(
        listOf(
            mapOf("model" to "Pixel2", "version" to appMinSdkVersion),
            mapOf("model" to "Pixel2", "version" to targetSdkVersion)
        )
    )
}

fladle {
    // Firebase Test Lab has min and max values that might differ from our project's
    // These are determined by `gcloud firebase test android models list`
    @Suppress("MagicNumber", "PropertyName", "VariableNaming")
    val FIREBASE_TEST_LAB_MIN_SDK = 23

    @Suppress("MagicNumber", "PropertyName", "VariableNaming")
    val FIREBASE_TEST_LAB_MAX_SDK = 33

    val minSdkVersion = run {
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

    configs {
        create("sanityConfigDebug") {
            clearPropertiesForSanityRobo()

            debugApk.set(
                project.provider {
                    "${buildDir}/outputs/apk/zcashmainnet/debug/ui-integration-test-zcashmainnet-debug.apk"
                }
            )

            testTimeout.set("3m")

            devices.addAll(
                mapOf("model" to "Pixel2", "version" to minSdkVersion),
                mapOf("model" to "Pixel2.arm", "version" to targetSdkVersion)
            )

            flankVersion.set(libs.versions.flank.get())
        }
    }
}