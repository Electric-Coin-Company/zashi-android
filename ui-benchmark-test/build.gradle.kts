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
        create("benchmark") {
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

// We provide this gradle task with benchmark tests only (debug-related tasks excluded). We still need to support debug
// variants to be compatible with debug variants in other modules, although benchmarking does not allow not minified
// build variants.
tasks.register("connectedBenchmarkTest") {
    group = "verification"
    dependsOn(
        "connectedZcashmainnetBenchmarkAndroidTest",
        "connectedZcashtestnetBenchmarkAndroidTest"
    )
}