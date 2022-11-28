plugins {
    id("com.android.library")
    kotlin("android")
    id("secant.android-build-conventions")
    id("wtf.emulator.gradle")
    id("secant.emulator-wtf-conventions")
    id("secant.jacoco-conventions")
}

// Note that we force enable test orchestrator for this module, because some of the test cases require it.
// Specifically this is needed due to checks on the UncaughtExceptionHandler tests

android {
    namespace = "co.electriccoin.zcash.crash.android"
    testNamespace = "co.electriccoin.zcash.crash.test"

    defaultConfig {
        testInstrumentationRunnerArguments["clearPackageData"] = "true"
    }

    testOptions {
        execution = "ANDROIDX_TEST_ORCHESTRATOR"
    }
}

dependencies {
    api(libs.androidx.annotation)
    api(projects.crashLib)

    implementation(platform(libs.firebase.bom))

    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.crashlytics.ndk)
    implementation(libs.firebase.installations)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.datetime)
    implementation(projects.spackleAndroidLib)

    androidTestImplementation(libs.bundles.androidx.test)
    androidTestImplementation(libs.kotlinx.coroutines.test)

    androidTestUtil(libs.androidx.test.services) {
        artifact {
            type = "apk"
        }
    }

    androidTestUtil(libs.androidx.test.orchestrator) {
        artifact {
            type = "apk"
        }
    }
}
