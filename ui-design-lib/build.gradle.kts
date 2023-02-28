plugins {
    id("com.android.library")
    kotlin("android")
    id("secant.android-build-conventions")
    id("secant.jacoco-conventions")
}

android {
    namespace = "co.electriccoin.zcash.ui.design"

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.androidx.compose.compiler.get().versionConstraint.displayName
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
    api(libs.kotlinx.immutable)
    implementation(projects.spackleAndroidLib)

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
        androidTestUtil(libs.androidx.test.orchestrator) {
            artifact {
                type = "apk"
            }
        }
    }
}
