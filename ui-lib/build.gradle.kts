plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-parcelize")
    id("zcash.android-build-conventions")
}

android {
    buildFeatures {
        viewBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.androidx.compose.compiler.get().versionConstraint.displayName
    }

    // TODO [#6]: Figure out how to move this into the build-conventions
    kotlinOptions {
        jvmTarget = libs.versions.java.get()
        allWarningsAsErrors = project.property("ZCASH_IS_TREAT_WARNINGS_AS_ERRORS").toString().toBoolean()
        freeCompilerArgs = freeCompilerArgs.plus("-Xopt-in=kotlin.RequiresOptIn")
    }

    sourceSets {
        getByName("main").apply {
            res.setSrcDirs(
                setOf(
                    "src/main/res/ui/backup",
                    "src/main/res/ui/common",
                    "src/main/res/ui/home",
                    "src/main/res/ui/onboarding",
                    "src/main/res/ui/profile",
                    "src/main/res/ui/restore",
                    "src/main/res/ui/seed",
                    "src/main/res/ui/settings",
                    "src/main/res/ui/wallet_address"
                )
            )
        }
    }
}

dependencies {
    implementation(libs.androidx.activity)
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.core)
    implementation(libs.androidx.lifecycle.livedata)
    implementation(libs.androidx.splash)
    implementation(libs.bundles.androidx.compose)
    implementation(libs.google.material)
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.zcash.sdk)
    implementation(libs.zcash.bip39)
    implementation(libs.zxing)

    implementation(projects.preferenceApiLib)
    implementation(projects.preferenceImplAndroidLib)
    implementation(projects.sdkExtLib)

    androidTestImplementation(libs.bundles.androidx.test)
    androidTestImplementation(libs.androidx.compose.test.junit)
    androidTestImplementation(libs.androidx.compose.test.manifest)
    androidTestImplementation(libs.kotlin.reflect)

    if (project.property("IS_USE_TEST_ORCHESTRATOR").toString().toBoolean()) {
        androidTestUtil(libs.androidx.test.orchestrator) {
            artifact {
                type = "apk"
            }
        }
    }
}
