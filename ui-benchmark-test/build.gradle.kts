plugins {
    id("com.android.test")
    kotlin("android")
    id("secant.android-build-conventions")
}

android {
    namespace = "co.electriccoin.zcash.ui.benchmark"
    targetProjectPath = ":${projects.app.name}"
    experimentalProperties["android.experimental.self-instrumenting"] = true

    defaultConfig {
        testInstrumentationRunner = "co.electriccoin.zcash.test.ZcashUiTestRunner"
        // to enable benchmarking for emulators, although only a physical device gives real results
        testInstrumentationRunnerArguments["androidx.benchmark.suppressErrors"] = "EMULATOR"
    }

    flavorDimensions.add("network")
    productFlavors {
        create("zcashtestnet") {
            dimension = "network"
        }
        create("zcashmainnet") {
            dimension = "network"
        }
    }

    buildTypes {
        create("release") {
            // To provide compatibility with other modules
        }
        create("benchmark") {
            // We provide the extra benchmark build variants for benchmarking. We still need to support debug
            // variants to be compatible with debug variants in other modules, although benchmarking does not allow
            // not minified build variants - benchmarking with the debug build variants will fail.
            isDebuggable = true
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks += listOf("release")
        }
    }
}

dependencies {
    implementation(projects.testLib)

    implementation(libs.bundles.androidx.test)
    implementation(libs.androidx.test.macrobenchmark)

    if (project.property("IS_USE_TEST_ORCHESTRATOR").toString().toBoolean()) {
        androidTestUtil(libs.androidx.test.orchestrator) {
            artifact {
                type = "apk"
            }
        }
    }
}