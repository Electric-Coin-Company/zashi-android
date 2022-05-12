plugins {
    id("com.android.library")
    kotlin("android")
    id("zcash.android-build-conventions")
    id("wtf.emulator.gradle")
    id("zcash.emulator-wtf-conventions")
}

android {
    // TODO [#6]: Figure out how to move this into the build-conventions
    kotlinOptions {
        jvmTarget = libs.versions.java.get()
        allWarningsAsErrors = project.property("ZCASH_IS_TREAT_WARNINGS_AS_ERRORS").toString().toBoolean()
        freeCompilerArgs = freeCompilerArgs.plus("-opt-in=kotlin.RequiresOptIn")
    }

    resourcePrefix = "co_electriccoin_zcash_"
}

dependencies {
    implementation(projects.sdkExtLib)

    implementation(libs.kotlinx.coroutines.core)

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