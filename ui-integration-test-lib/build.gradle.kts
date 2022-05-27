plugins {
  id("com.android.library")
  kotlin("android")
  id("zcash.android-build-conventions")
  id("wtf.emulator.gradle")
  id("zcash.emulator-wtf-conventions")
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

  androidTestImplementation(libs.bundles.androidx.test)
  androidTestImplementation(libs.bundles.androidx.compose.core)

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

// This block exists to override the minimum SDK version for our integration tests from the property
// ANDROID_LIB_MIN_SDK_VERSION to ANDROID_APP_MIN_SDK_VERSION, as by our integration tests we aim to
// test the UI of the app.
// TODO [#448]: https://github.com/zcash/secant-android-wallet/issues/448
emulatorwtf {
  val minSdkVersion = project.properties["ANDROID_APP_MIN_SDK_VERSION"].toString().toInt()
  val maxSdkVersion = project.properties["ANDROID_TARGET_SDK_VERSION"].toString().toInt()

  devices.set(
      listOf(
          mapOf("model" to "Pixel2", "version" to minSdkVersion),
          mapOf("model" to "Pixel2", "version" to maxSdkVersion)
      )
  )
}