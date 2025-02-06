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

    api(libs.bundles.koin)

    debugImplementation(platform(libs.firebase.bom))
    releaseImplementation(platform(libs.firebase.bom))

    debugImplementation(libs.firebase.crashlytics)
    releaseImplementation(libs.firebase.crashlytics)

    // NOTE: couldn't make it working this way
    // implementation(libs.firebase.crashlytics)
    // fossImplementation(libs.firebase.crashlytics) {
    //     exclude("com.google.firebase", "firebase-crashlytics-ktx")
    // }

    debugImplementation(libs.firebase.crashlytics.ndk)
    releaseImplementation(libs.firebase.crashlytics.ndk)

    debugImplementation(libs.firebase.installations)
    releaseImplementation(libs.firebase.installations)

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
