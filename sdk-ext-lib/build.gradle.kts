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

// This block can eventually go away; it exists to override the max SDK version to avoid a JNI
// crash https://github.com/zcash/secant-android-wallet/issues/430
emulatorwtf {
    // Emulator WTF has min and max values that might differ from our project's
    // These are determined by `ew-cli --models`
    @Suppress("MagicNumber", "PropertyName", "VariableNaming")
    val EMULATOR_WTF_MIN_API = 23

    val minSdkVersion = run {
        val buildMinSdk = project.properties["ANDROID_LIB_MIN_SDK_VERSION"].toString().toInt()
        buildMinSdk.coerceAtLeast(EMULATOR_WTF_MIN_API).toString()
    }

    devices.set(
        listOf(
            mapOf("model" to "Pixel2", "version" to minSdkVersion),
            @Suppress("MagicNumber")
            mapOf("model" to "Pixel2", "version" to 30)
        )
    )
}
