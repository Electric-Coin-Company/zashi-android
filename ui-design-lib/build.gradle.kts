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
        freeCompilerArgs = freeCompilerArgs.plus("-opt-in=kotlin.RequiresOptIn")
    }

    sourceSets {
        getByName("main").apply {
            res.setSrcDirs(
                setOf(
                    "src/main/res/ui/common",
                )
            )
        }
    }
}

dependencies {
    implementation(libs.androidx.annotation)
    implementation(libs.androidx.core)
    implementation(libs.androidx.splash)
    implementation(libs.bundles.androidx.compose.core)
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.coroutines.core)
    implementation(projects.spackleLib)

    androidTestImplementation(libs.bundles.androidx.test)
    androidTestImplementation(libs.androidx.compose.test.junit)
    androidTestImplementation(libs.androidx.compose.test.manifest)
    androidTestImplementation(libs.kotlin.reflect)
    androidTestImplementation(libs.kotlin.test)

    if (project.property("IS_USE_TEST_ORCHESTRATOR").toString().toBoolean()) {
        androidTestUtil(libs.androidx.test.orchestrator) {
            artifact {
                type = "apk"
            }
        }
    }
}
