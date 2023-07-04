plugins {
    id("com.android.library")
    kotlin("android")
    id("secant.android-build-conventions")
    id("wtf.emulator.gradle")
    id("secant.emulator-wtf-conventions")
    id("secant.jacoco-conventions")
}

android {
    namespace = "co.electriccoin.zcash.ui"

    defaultConfig {
        testInstrumentationRunner = "co.electriccoin.zcash.test.ZcashUiTestRunner"
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.androidx.compose.compiler.get().versionConstraint.displayName
    }

    sourceSets {
        getByName("main").apply {
            res.setSrcDirs(
                setOf(
                    "src/main/res/ui/about",
                    "src/main/res/ui/backup",
                    "src/main/res/ui/common",
                    "src/main/res/ui/history",
                    "src/main/res/ui/home",
                    "src/main/res/ui/onboarding",
                    "src/main/res/ui/receive",
                    "src/main/res/ui/request",
                    "src/main/res/ui/restore",
                    "src/main/res/ui/scan",
                    "src/main/res/ui/seed",
                    "src/main/res/ui/send",
                    "src/main/res/ui/settings",
                    "src/main/res/ui/support",
                    "src/main/res/ui/update",
                    "src/main/res/ui/wallet_address",
                    "src/main/res/ui/warning",
                )
            )
        }
    }
}

dependencies {
    implementation(libs.accompanist.permissions)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.livedata)
    implementation(libs.androidx.splash)
    implementation(libs.androidx.workmanager)
    implementation(libs.bundles.androidx.camera)
    implementation(libs.bundles.androidx.compose.core)
    implementation(libs.bundles.androidx.compose.extended)
    implementation(libs.bundles.play.update)
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.guava)
    implementation(libs.kotlinx.datetime)
    implementation(libs.kotlinx.immutable)
    implementation(libs.zcash.sdk)
    implementation(libs.zcash.sdk.incubator)
    implementation(libs.zcash.bip39)
    implementation(libs.zxing)

    implementation(projects.buildInfoLib)
    implementation(projects.configurationApiLib)
    implementation(projects.configurationImplAndroidLib)
    implementation(projects.crashAndroidLib)
    implementation(projects.preferenceApiLib)
    implementation(projects.preferenceImplAndroidLib)
    implementation(projects.sdkExtLib)
    implementation(projects.spackleAndroidLib)
    api(projects.uiDesignLib)

    androidTestImplementation(projects.testLib)
    androidTestImplementation(libs.bundles.androidx.test)
    androidTestImplementation(libs.androidx.compose.test.junit)
    androidTestImplementation(libs.androidx.compose.test.manifest)
    androidTestImplementation(libs.kotlin.reflect)
    androidTestImplementation(libs.kotlin.test)

    androidTestUtil(libs.androidx.test.services) {
        artifact {
            type = "apk"
        }
    }

    if (project.property("IS_USE_TEST_ORCHESTRATOR").toString().toBoolean()) {
        androidTestUtil(libs.androidx.test.services)
        androidTestUtil(libs.androidx.test.orchestrator) {
            artifact {
                type = "apk"
            }
        }
    }
}

