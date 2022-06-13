enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        val isRepoRestrictionEnabled = true

        mavenCentral {
            if (isRepoRestrictionEnabled) {
                content {
                    includeGroup("wtf.emulator")
                }
            }
        }
        google {
            if (isRepoRestrictionEnabled) {
                content {
                    includeGroup("androidx.navigation")
                    includeGroup("com.android.tools")
                    includeGroup("com.google.testing.platform")
                    includeGroupByRegex("androidx.*")
                    includeGroupByRegex("com\\.android.*")
                    includeGroupByRegex("com\\.android\\.tools.*")
                }
            }
        }
        gradlePluginPortal {
            if (isRepoRestrictionEnabled) {
                content {
                    excludeGroup("androidx.navigation")
                    excludeGroup("com.android.tools")
                    excludeGroup("com.google.testing.platform")
                    excludeGroupByRegex("androidx.*")
                    excludeGroupByRegex("com\\.android.*")
                    excludeGroupByRegex("com\\.android\\.tools.*")
                }
            }
        }
    }

    plugins {
        val androidGradlePluginVersion = extra["ANDROID_GRADLE_PLUGIN_VERSION"].toString()
        val kotlinVersion = extra["KOTLIN_VERSION"].toString()

        id("com.android.application") version (androidGradlePluginVersion) apply (false)
        id("com.android.library") version (androidGradlePluginVersion) apply (false)
        id("com.github.ben-manes.versions") version (extra["GRADLE_VERSIONS_PLUGIN_VERSION"].toString()) apply (false)
        id("com.github.triplet.play") version (extra["PLAY_PUBLISHER_PLUGIN_VERSION"].toString()) apply (false)
        id("com.osacky.fulladle") version (extra["FULLADLE_VERSION"].toString()) apply (false)
        id("io.gitlab.arturbosch.detekt") version (extra["DETEKT_VERSION"].toString()) apply (false)
        id("org.jetbrains.kotlinx.kover") version (extra["KOVER_VERSION"].toString()) apply (false)
        id("wtf.emulator.gradle") version (extra["EMULATOR_WTF_GRADLE_PLUGIN_VERSION"].toString()) apply (false)
        kotlin("android") version (kotlinVersion) apply (false)
        kotlin("jvm") version (kotlinVersion)
        kotlin("multiplatform") version (kotlinVersion)
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    @Suppress("UnstableApiUsage")
    repositories {
        val isRepoRestrictionEnabled = true

        google {
            if (isRepoRestrictionEnabled) {
                content {
                    includeGroup("android.arch.core")
                    includeGroup("android.arch.lifecycle")
                    includeGroup("com.google.android.material")
                    includeGroup("com.google.testing.platform")
                    includeGroup("com.google.android.play")
                    includeGroupByRegex("androidx.*")
                    includeGroupByRegex("com\\.android.*")
                }
            }
        }
        mavenCentral {
            if (isRepoRestrictionEnabled) {
                content {
                    excludeGroup("android.arch.lifecycle")
                    excludeGroup("android.arch.core")
                    excludeGroup("wtf.emulator")
                    excludeGroup("com.google.android.material")
                    excludeGroup("com.google.android.play")
                    excludeGroupByRegex("androidx.*")
                    excludeGroupByRegex("com\\.android.*")
                }
            }
        }
        maven("https://oss.sonatype.org/content/repositories/snapshots") {
            mavenContent {
                snapshotsOnly()
            }
            if (isRepoRestrictionEnabled) {
                content {
                    includeGroup("cash.z.ecc.android")
                }
            }
        }
        maven("https://maven.emulator.wtf/releases/") {
            if (isRepoRestrictionEnabled) {
                content {
                    includeGroup("wtf.emulator")
                }
            }
        }
    }

    @Suppress("UnstableApiUsage", "MaxLineLength")
    versionCatalogs {
        create("libs") {
            val accompanistPermissionsVersion = extra["ACCOMPANIST_PERMISSIONS_VERSION"].toString()
            val androidxActivityVersion = extra["ANDROIDX_ACTIVITY_VERSION"].toString()
            val androidxAnnotationVersion = extra["ANDROIDX_ANNOTATION_VERSION"].toString()
            val androidxAppcompatVersion = extra["ANDROIDX_APPCOMPAT_VERSION"].toString()
            val androidxCameraVersion = extra["ANDROIDX_CAMERA_VERSION"].toString()
            val androidxComposeCompilerVersion = extra["ANDROIDX_COMPOSE_COMPILER_VERSION"].toString()
            val androidxComposeMaterial3Version = extra["ANDROIDX_COMPOSE_MATERIAL3_VERSION"].toString()
            val androidxComposeVersion = extra["ANDROIDX_COMPOSE_VERSION"].toString()
            val androidxConstraintlayoutVersion = extra["ANDROIDX_CONSTRAINTLAYOUT_VERSION"].toString()
            val androidxCoreVersion = extra["ANDROIDX_CORE_VERSION"].toString()
            val androidxEspressoVersion = extra["ANDROIDX_ESPRESSO_VERSION"].toString()
            val androidxLifecycleVersion = extra["ANDROIDX_LIFECYCLE_VERSION"].toString()
            val androidxNavigationComposeVersion = extra["ANDROIDX_NAVIGATION_COMPOSE_VERSION"].toString()
            val androidxSecurityCryptoVersion = extra["ANDROIDX_SECURITY_CRYPTO_VERSION"].toString()
            val androidxSplashScreenVersion = extra["ANDROIDX_SPLASH_SCREEN_VERSION"].toString()
            val androidxTestCoreVersion = extra["ANDROIDX_TEST_CORE_VERSION"].toString()
            val androidxTestJunitVersion = extra["ANDROIDX_TEST_JUNIT_VERSION"].toString()
            val androidxTestOrchestratorVersion = extra["ANDROIDX_TEST_ORCHESTRATOR_VERSION"].toString()
            val androidxTestRunnerVersion = extra["ANDROIDX_TEST_RUNNER_VERSION"].toString()
            val androidxUiAutomatorVersion = extra["ANDROIDX_UI_AUTOMATOR_VERSION"].toString()
            val androidxWorkManagerVersion = extra["ANDROIDX_WORK_MANAGER_VERSION"].toString()
            val coreLibraryDesugaringVersion = extra["CORE_LIBRARY_DESUGARING_VERSION"].toString()
            val flankVersion = extra["FLANK_VERSION"].toString()
            val jacocoVersion = extra["JACOCO_VERSION"].toString()
            val javaVersion = extra["ANDROID_JVM_TARGET"].toString()
            val kotlinVersion = extra["KOTLIN_VERSION"].toString()
            val kotlinxDateTimeVersion = extra["KOTLINX_DATETIME_VERSION"].toString()
            val kotlinxCoroutinesVersion = extra["KOTLINX_COROUTINES_VERSION"].toString()
            val playCoreVersion = extra["PLAY_CORE_VERSION"].toString()
            val playCoreKtxVersion = extra["PLAY_CORE_KTX_VERSION"].toString()
            val zcashBip39Version = extra["ZCASH_BIP39_VERSION"].toString()
            val zcashSdkVersion = extra["ZCASH_SDK_VERSION"].toString()
            val zxingVersion = extra["ZXING_VERSION"].toString()

            // Standalone versions
            version("flank", flankVersion)
            version("jacoco", jacocoVersion)
            version("java", javaVersion)

            // Aliases
            library("accompanist-permissions", "com.google.accompanist:accompanist-permissions:$accompanistPermissionsVersion")
            library("androidx-activity", "androidx.activity:activity-ktx:$androidxActivityVersion")
            library("androidx-activity-compose", "androidx.activity:activity-compose:$androidxActivityVersion")
            library("androidx-annotation", "androidx.annotation:annotation:$androidxAnnotationVersion")
            library("androidx-appcompat", "androidx.appcompat:appcompat:$androidxAppcompatVersion")
            library("androidx-camera", "androidx.camera:camera-camera2:$androidxCameraVersion")
            library("androidx-camera-lifecycle", "androidx.camera:camera-lifecycle:$androidxCameraVersion")
            library("androidx-camera-view", "androidx.camera:camera-view:$androidxCameraVersion")
            library("androidx-compose-foundation", "androidx.compose.foundation:foundation:$androidxComposeVersion")
            library("androidx-compose-material3", "androidx.compose.material3:material3:$androidxComposeMaterial3Version")
            library("androidx-compose-material-icons-core", "androidx.compose.material:material-icons-core:$androidxComposeVersion")
            library("androidx-compose-material-icons-extended", "androidx.compose.material:material-icons-extended:$androidxComposeVersion")
            library("androidx-compose-tooling", "androidx.compose.ui:ui-tooling:$androidxComposeVersion")
            library("androidx-compose-ui", "androidx.compose.ui:ui:$androidxComposeVersion")
            library("androidx-compose-compiler", "androidx.compose.compiler:compiler:$androidxComposeCompilerVersion")
            library("androidx-core", "androidx.core:core-ktx:$androidxCoreVersion")
            library("androidx-constraintlayout", "androidx.constraintlayout:constraintlayout-compose:$androidxConstraintlayoutVersion")
            library("androidx-lifecycle-livedata", "androidx.lifecycle:lifecycle-livedata-ktx:$androidxLifecycleVersion")
            library("androidx-navigation-compose", "androidx.navigation:navigation-compose:$androidxNavigationComposeVersion")
            library("androidx-security-crypto", "androidx.security:security-crypto-ktx:$androidxSecurityCryptoVersion")
            library("androidx-splash", "androidx.core:core-splashscreen:$androidxSplashScreenVersion")
            library("androidx-viewmodel-compose", "androidx.lifecycle:lifecycle-viewmodel-compose:$androidxLifecycleVersion")
            library("androidx-workmanager", "androidx.work:work-runtime-ktx:$androidxWorkManagerVersion")
            library("desugaring", "com.android.tools:desugar_jdk_libs:$coreLibraryDesugaringVersion")
            library("kotlin-stdlib", "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
            library("kotlin-reflect", "org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
            library("kotlin-test", "org.jetbrains.kotlin:kotlin-test:$kotlinVersion")
            library("kotlinx-coroutines-android", "org.jetbrains.kotlinx:kotlinx-coroutines-android:$kotlinxCoroutinesVersion")
            library("kotlinx-coroutines-core", "org.jetbrains.kotlinx:kotlinx-coroutines-core:$kotlinxCoroutinesVersion")
            library("kotlinx-coroutines-guava", "org.jetbrains.kotlinx:kotlinx-coroutines-guava:$kotlinxCoroutinesVersion")
            library("kotlinx-datetime", "org.jetbrains.kotlinx:kotlinx-datetime:$kotlinxDateTimeVersion")
            library("play-core", "com.google.android.play:core:$playCoreVersion")
            library("play-core-ktx", "com.google.android.play:core-ktx:$playCoreKtxVersion")
            library("zcash-sdk", "cash.z.ecc.android:zcash-android-sdk:$zcashSdkVersion")
            library("zcash-bip39", "cash.z.ecc.android:kotlin-bip39:$zcashBip39Version")
            library("zcash-walletplgns", "cash.z.ecc.android:zcash-android-wallet-plugins:$zcashBip39Version")
            library("zxing", "com.google.zxing:core:$zxingVersion")

            // Test libraries
            library("androidx-compose-test-junit", "androidx.compose.ui:ui-test-junit4:$androidxComposeVersion")
            library("androidx-compose-test-manifest", "androidx.compose.ui:ui-test-manifest:$androidxComposeVersion")
            // Cannot use espresso-contrib, because it causes a build failure
            //alias("androidx-espresso-contrib", "androidx.test.espresso:espresso-contrib:$androidxEspressoVersion")
            library("androidx-espresso-core", "androidx.test.espresso:espresso-core:$androidxEspressoVersion")
            library("androidx-espresso-intents", "androidx.test.espresso:espresso-intents:$androidxEspressoVersion")
            library("androidx-test-core", "androidx.test:core-ktx:$androidxTestCoreVersion")
            library("androidx-test-junit", "androidx.test.ext:junit-ktx:$androidxTestJunitVersion")
            library("androidx-test-orchestrator", "androidx.test:orchestrator:$androidxTestOrchestratorVersion")
            library("androidx-test-runner", "androidx.test:runner:$androidxTestRunnerVersion")
            library("androidx-uiAutomator", "androidx.test.uiautomator:uiautomator-v18:$androidxUiAutomatorVersion")
            library("kotlinx-coroutines-test", "org.jetbrains.kotlinx:kotlinx-coroutines-test:$kotlinxCoroutinesVersion")

            // Bundles
            bundle(
                "androidx-camera",
                listOf(
                    "androidx-camera",
                    "androidx-camera-lifecycle",
                    "androidx-camera-view"
                )
            )
            bundle(
                "androidx-compose-core",
                listOf(
                    "androidx-compose-compiler",
                    "androidx-compose-foundation",
                    "androidx-compose-material3",
                    "androidx-compose-tooling",
                    "androidx-compose-ui",
                )
            )
            bundle(
                "androidx-compose-extended",
                listOf(
                    "androidx-activity-compose",
                    "androidx-compose-material-icons-core",
                    "androidx-compose-material-icons-extended",
                    "androidx-navigation-compose",
                    "androidx-viewmodel-compose"
                )
            )
            bundle(
                "play-core",
                listOf(
                    "play-core",
                    "play-core-ktx",
                )
            )
            bundle(
                "androidx-test",
                listOf(
                    "androidx-espresso-core",
                    "androidx-espresso-intents",
                    "androidx-test-core",
                    "androidx-test-junit",
                    "androidx-test-runner"
                )
            )
        }
    }
}

rootProject.name = "zcash-android-app"

includeBuild("build-conventions-secant")

include("app")
include("build-info-lib")
include("crash-lib")
include("crash-android-lib")
include("preference-api-lib")
include("preference-impl-android-lib")
include("sdk-ext-lib")
include("sdk-ext-ui-lib")
include("spackle-lib")
include("spackle-android-lib")
include("test-lib")
include("ui-design-lib")
include("ui-integration-test-lib")
include("ui-lib")

val zcashSdkIncludedBuildPath = extra["SDK_INCLUDED_BUILD_PATH"].toString()

if (zcashSdkIncludedBuildPath.isNotEmpty()) {
    logger.lifecycle("The SDK will be used from $zcashSdkIncludedBuildPath instead of Maven Central.")
    includeBuild(zcashSdkIncludedBuildPath) {
        dependencySubstitution {
            substitute(module("cash.z.ecc.android:zcash-android-sdk")).using(project(":sdk-lib"))
        }
    }
}

val bip39IncludedBuildPath = extra["BIP_39_INCLUDED_BUILD_PATH"].toString()

if (bip39IncludedBuildPath.isNotEmpty()) {
    logger.lifecycle("BIP-39 will be used from $bip39IncludedBuildPath instead of Maven Central.")
    includeBuild(bip39IncludedBuildPath) {
        dependencySubstitution {
            substitute(module("cash.z.ecc.android:kotlin-bip39")).using(project(":bip39-lib"))
        }
    }
}