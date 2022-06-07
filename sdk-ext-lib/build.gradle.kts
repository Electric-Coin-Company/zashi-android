plugins {
    id("com.android.library")
    kotlin("android")
    id("zcash.android-build-conventions")
    id("wtf.emulator.gradle")
    id("zcash.emulator-wtf-conventions")
}

android {
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    api(libs.zcash.sdk)
    api(libs.zcash.bip39)

    androidTestImplementation(libs.bundles.androidx.test)
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.kotlin.test)

    if (project.property("IS_USE_TEST_ORCHESTRATOR").toString().toBoolean()) {
        androidTestUtil(libs.androidx.test.orchestrator) {
            artifact {
                type = "apk"
            }
        }
    }
}

