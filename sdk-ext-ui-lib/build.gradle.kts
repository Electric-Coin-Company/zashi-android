plugins {
    id("com.android.library")
    kotlin("android")
    id("secant.android-build-conventions")
    id("wtf.emulator.gradle")
    id("secant.emulator-wtf-conventions")
}

android {
    namespace = "cash.z.ecc.sdk.ext.ui"
    testNamespace = "cash.z.ecc.sdk.ext.ui.test"
    resourcePrefix = "co_electriccoin_zcash_"

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
    }
}

dependencies {
    coreLibraryDesugaring(libs.desugaring)
    implementation(projects.sdkExtLib)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.datetime)

    androidTestImplementation(libs.bundles.androidx.test)
    androidTestImplementation(libs.kotlin.test)

    if (project.property("IS_USE_TEST_ORCHESTRATOR").toString().toBoolean()) {
        androidTestUtil(libs.androidx.test.orchestrator) {
            artifact {
                type = "apk"
            }
        }
    }
}