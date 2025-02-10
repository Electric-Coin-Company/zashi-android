import model.DistributionDimension
import model.BuildType
import model.NetworkDimension

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
        // To simplify module variants, we assume to run benchmarking against mainnet only
        missingDimensionStrategy(NetworkDimension.DIMENSION_NAME, NetworkDimension.MAINNET.value)
        missingDimensionStrategy(DistributionDimension.DIMENSION_NAME, DistributionDimension.STORE.value)
        missingDimensionStrategy(DistributionDimension.DIMENSION_NAME, DistributionDimension.FOSS.value)
    }

    buildTypes {
        create(BuildType.RELEASE.value) {
            // To provide compatibility with other modules
        }
        create(BuildType.BENCHMARK.value) {
            // We provide the extra benchmark build variants for benchmarking. We still need to support debug
            // variants to be compatible with debug variants in other modules, although benchmarking does not allow
            // not minified build variants - benchmarking with the debug build variants will fail.
            isDebuggable = true
            signingConfig = signingConfigs.getByName(BuildType.DEBUG.value)
            matchingFallbacks += listOf(BuildType.RELEASE.value)
        }
    }
}

dependencies {
    implementation(projects.testLib)

    implementation(libs.bundles.androidx.test)
    implementation(libs.androidx.test.macrobenchmark)

    androidTestUtil(libs.androidx.test.services) {
        artifact {
            type = "apk"
        }
    }

    if (project.property("IS_USE_TEST_ORCHESTRATOR").toString().toBoolean()) {
        androidTestUtil(libs.androidx.test.orchestrator) {
            artifact {
                type = "apk"
            }
        }
    }
}