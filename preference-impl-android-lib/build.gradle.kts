plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-parcelize")
    id("zcash.android-build-conventions")
}

// Force orchestrator to be used for this module, because we need the preference files
// to be purged between tests
val isOrchestratorEnabled = true

android {
    // TODO [#6]: Figure out how to move this into the build-conventions
    kotlinOptions {
        jvmTarget = libs.versions.java.get()
        allWarningsAsErrors = project.property("ZCASH_IS_TREAT_WARNINGS_AS_ERRORS").toString().toBoolean()
        freeCompilerArgs = freeCompilerArgs + "-opt-in=kotlin.RequiresOptIn"
    }


    if (isOrchestratorEnabled) {
        defaultConfig {
            testInstrumentationRunnerArguments["clearPackageData"] = "true"
        }

        testOptions {
            execution = "ANDROIDX_TEST_ORCHESTRATOR"
        }
    }
}

dependencies {
    implementation(libs.androidx.security.crypto)
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    implementation(projects.preferenceApiLib)

    androidTestImplementation(libs.bundles.androidx.test)
    androidTestImplementation(libs.kotlinx.coroutines.test)

    if (isOrchestratorEnabled) {
        androidTestUtil(libs.androidx.test.orchestrator) {
            artifact {
                type = "apk"
            }
        }
    }
}
