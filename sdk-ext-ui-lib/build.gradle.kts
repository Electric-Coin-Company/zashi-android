plugins {
    id("com.android.library")
    kotlin("android")
    id("zcash.android-build-conventions")
}

android {
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