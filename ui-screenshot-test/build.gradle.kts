plugins {
    id("com.android.test")
    kotlin("android")
    id("secant.android-build-conventions")
    id("wtf.emulator.gradle")
    id("secant.emulator-wtf-conventions")
}

// Force orchestrator to be used for this module, because we need cleared state to generate screenshots
val isOrchestratorEnabled = true

android {
    namespace = "co.electroniccoin.zcash.ui.screenshot"
    // Target needs to be set to com.android.application type module
    targetProjectPath = ":${projects.app.name}"

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
    buildTypes {
        create("release") {
            // to align with the benchmark module requirement - run against minified application
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
    implementation(projects.testLib)
    implementation(projects.spackleAndroidLib)
    implementation(projects.sdkExtLib)
    implementation(projects.sdkExtUiLib)

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
}