plugins {
    id("com.android.library")
    kotlin("android")
    id("secant.android-build-conventions")
    id("wtf.emulator.gradle")
    id("secant.emulator-wtf-conventions")
}

// Force orchestrator to be used for this module, because we need cleared state to generate screenshots
val isOrchestratorEnabled = true

android {
    defaultConfig {
        if (isOrchestratorEnabled) {
            testInstrumentationRunnerArguments["clearPackageData"] = "true"
        }

        testInstrumentationRunner = "co.electriccoin.zcash.test.ZcashUiTestRunner"
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
    androidTestImplementation(projects.uiLib)
    androidTestImplementation(projects.uiDesignLib)
    androidTestImplementation(projects.testLib)
    androidTestImplementation(projects.spackleAndroidLib)

    androidTestImplementation(libs.bundles.androidx.test)
    androidTestImplementation(libs.bundles.androidx.compose.core)
    androidTestImplementation(libs.bundles.play.core)

    androidTestImplementation(libs.androidx.compose.test.junit)
    androidTestImplementation(libs.androidx.navigation.compose)
    androidTestImplementation(libs.androidx.uiAutomator)

    if (isOrchestratorEnabled) {
        androidTestUtil(libs.androidx.test.orchestrator) {
            artifact {
                type = "apk"
            }
        }
    }
}